

For Amazon Linux 2 systems
Launch 2 amazon linux 2 machines
1 master and 1 slaves

On both master and slave nodes :

    sudo su 
    yum install docker -y 
    systemctl enable docker && systemctl start docker
------------------------------------------------------------------
Only for installing Docker CE on CentOS 7 on AWS
	1. sudo yum install -y yum-utils device-mapper-persistent-data lvm2
  
	2. sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
	
	3. sudo yum-config-manager --enable docker-ce-edge
	4. sudo yum-config-manager --enable docker-ce-test
	5. yum install -y https://download.docker.com/linux/centos/7/x86_64/stable/Packages/containerd.io-1.2.6-3.3.el7.x86_64.rpm
	6. sudo yum install docker-ce
	7. systemctl start docker
	8. systemctl enable docker

------------------------------------------------------------------


cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://packages.cloud.google.com/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=0
gpgkey=https://packages.cloud.google.com/yum/doc/yum-key.gpg https://packages.cloud.google.com/yum/doc/rpm-package-key.gpg
exclude=kube*
EOF

    cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
    EOF
    sysctl --system

    setenforce 0

### install kubelet, kubeadm and kubectl; start kubelet daemon
### Do it on both master as welll as worker nodes 

    yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes

    systemctl enable kubelet && systemctl start kubelet

### On master node  initialize the cluster 

    sudo kubeadm init --pod-network-cidr=192.168.0.0/16 --ignore-preflight-errors=NumCPU
    #  sudo kubeadm init --pod-network-cidr=192.168.0.0/16 #Do this only if proper CPU cores are available
    mkdir -p $HOME/.kube
    sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
    sudo chown $(id -u):$(id -g) $HOME/.kube/config
    export KUBECONFIG=/etc/kubernetes/kubelet.conf

## On Worker nodes, Switch to the root mode
Copy kubeadm join command from output of "kubeadm init on master node" 
   
    <kubeadm join command copies from master node>

## Clone a repository to get YAML files for Calico networking

	kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml

## For calico networking: apply on master node only

    sudo kubectl apply -f etcd.yaml
    sudo kubectl apply -f rbac.yaml
    sudo kubectl apply -f calico.yaml
    

Take a pause of 2 minutes and see if the nodes are ready; run it on master node

    kubectl get nodes

watch system pods

    kubectl get pods --all-namespaces


on all the worker nodes do 

    mkdir -p $HOME/.kube
    export KUBECONFIG=/etc/kubernetes/kubelet.conf