#!/bin/bash
set -e

KEY_PATH="$HOME/.ssh/do_server_monitor"
INFRA_DIR="terraform/infra"
DOCKER_DIR="terraform/deploy"
ANSIBLE_DIR="ansible"

echo "══════════════════════════════════════"
echo " 1. Déploiement de l'infra DigitalOcean"
echo "══════════════════════════════════════"
cd $INFRA_DIR
terraform init -upgrade
terraform apply -auto-approve -var="do_token=$DO_TOKEN"

DROPLET_IP=$(terraform output -raw droplet_ip)
echo "✅ Droplet créé : $DROPLET_IP"

echo ""
echo "══════════════════════════════════════"
echo " 2. Attente que le Droplet soit prêt"
echo "══════════════════════════════════════"
echo "⏳ Attente 40s..."
sleep 40

# Test SSH jusqu'à ce que ça réponde
MAX_RETRIES=10
COUNT=0
until ssh -i $KEY_PATH -o StrictHostKeyChecking=no -o ConnectTimeout=5 root@$DROPLET_IP "echo OK" 2>/dev/null; do
  COUNT=$((COUNT+1))
  if [ $COUNT -ge $MAX_RETRIES ]; then
    echo "❌ Impossible de joindre le droplet après $MAX_RETRIES tentatives"
    exit 1
  fi
  echo "🔄 Tentative $COUNT/$MAX_RETRIES — nouvelle tentative dans 10s..."
  sleep 10
done
echo "✅ Droplet accessible via SSH"

echo ""
echo "══════════════════════════════════════"
echo " 3. Injection de l'IP dans Ansible"
echo "══════════════════════════════════════"
cd ../../$ANSIBLE_DIR
echo "[servers]
droplet ansible_host=$DROPLET_IP ansible_user=root ansible_ssh_private_key_file=$KEY_PATH" > inventory.ini
echo "✅ inventory.ini mis à jour : $DROPLET_IP"

echo ""
echo "══════════════════════════════════════"
echo " 4. Exécution du playbook Ansible"
echo "══════════════════════════════════════"
ansible-playbook -i inventory.ini playbook.yml
echo "✅ Serveur prêt"

echo ""
echo "══════════════════════════════════════"
echo " 5. Déploiement des conteneurs Docker"
echo "══════════════════════════════════════"
cd ../$DOCKER_DIR
terraform init -upgrade
terraform apply -auto-approve -var="droplet_ip=$DROPLET_IP"

echo ""
echo "══════════════════════════════════════"
echo "✅ Déploiement terminé !"
echo "   Frontend : http://$DROPLET_IP:8081"
echo "   Backend  : http://$DROPLET_IP:81"
echo "══════════════════════════════════════"