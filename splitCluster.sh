#!/bin/bash

# Split x from a + b
iptables -A INPUT -p all -s 127.0.0.1 -d 127.0.0.30 -j DROP
iptables -A INPUT -p all -s 127.0.0.30 -d 127.0.0.1 -j DROP
iptables -A INPUT -p all -s 127.0.0.1 -d 127.0.0.31 -j DROP
iptables -A INPUT -p all -s 127.0.0.31 -d 127.0.0.1 -j DROP

# Split y from a + b
iptables -A INPUT -p all -s 127.0.0.2 -d 127.0.0.30 -j DROP
iptables -A INPUT -p all -s 127.0.0.30 -d 127.0.0.2 -j DROP
iptables -A INPUT -p all -s 127.0.0.2 -d 127.0.0.31 -j DROP
iptables -A INPUT -p all -s 127.0.0.31 -d 127.0.0.2 -j DROP

# Split z from a + b
iptables -A INPUT -p all -s 127.0.0.3 -d 127.0.0.30 -j DROP
iptables -A INPUT -p all -s 127.0.0.30 -d 127.0.0.3 -j DROP
iptables -A INPUT -p all -s 127.0.0.3 -d 127.0.0.31 -j DROP
iptables -A INPUT -p all -s 127.0.0.31 -d 127.0.0.3 -j DROP
