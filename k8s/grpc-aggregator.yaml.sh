#!/bin/bash

cat <<YAML
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: grpc-aggregator
spec:
  replicas: 1
  strategy:
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: grpc-aggregator
    spec:
      containers:
        - name: grpc-aggregator
          image: gcr.io/$GCP_PROJECT/grpc-aggregator:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
            - containerPort: 9999
          env:
            - name: peremoga_host
              value: "grpc-content-a"
            - name: peremoga_port
              value: "8080"
            - name: zrada_host
              value: "grpc-content-b"
            - name: zrada_port
              value: "8080"
---
apiVersion: v1
kind: Service
metadata:
  name: grpc-aggregator
spec:
  type: NodePort
  selector:
    app: grpc-aggregator
  ports:
   - port: 8080
     targetPort: 8080
     protocol: TCP
---
YAML