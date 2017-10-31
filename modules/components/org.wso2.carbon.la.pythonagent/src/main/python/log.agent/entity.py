# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

import constants
import json

class Topology:
    """
    Represents the topology provided by the Cloud Controller
    """

    def __init__(self):
        self.service_map = {}
        """ :type : dict[str, Service]  """
        self.initialized = False
        """ :type : bool  """
        self.json_str = None
        """ :type : str  """

    def get_services(self):
        """
        Provides the list of services on the topology
        :return: The list of Service objects
        :rtype: list[Service]
        """
        return self.service_map.values()

    def get_service(self, service_name):
        """
        Provides the service information for the given service name
        :param str service_name: service name to be retrieved
        :return: Service object of the service, None if the provided service name is invalid
        :rtype: Service
        """
        if service_name in self.service_map:
            return self.service_map[service_name]

        return None

    def add_service(self, service):
        """
        Adds a service to the list of services on the topology

        :param Service service:
        :return: void
        """
        self.service_map[service.service_name] = service

    def add_services(self, services):
        """

        :param list[Service] services:
        :return: void
        """
        for service in services:
            self.add_service(service)

    def remove_service(self, service_name):
        """
        Removes the service of the provided service name
        :param str service_name:
        :return: void
        """
        if service_name in self.service_map:
            self.service_map.pop(service_name)

    def service_exists(self, service_name):
        """
        Checks if the service of the provided service name exists
        :param str service_name:
        :return: True if the service exists, False if otherwise
        :rtype: bool
        """
        return service_name in self.service_map

    def clear(self):
        """
        Clears the service information list
        :return: void
        """
        self.service_map = {}

    def __str__(self):
        """
        to string override
        :return:
        """
        return "Topology [serviceMap= %r , initialized= %r ]" % (self.service_map, self.initialized)


class Service:
    """
    Represents a service on the topology
    """

    def __init__(self, service_name, service_type):
        self.service_name = service_name
        """ :type : str  """
        self.service_type = service_type
        """ :type : str  """
        self.cluster_id_cluster_map = {}
        """ :type : dict[str, Cluster]  """
        self.port_map = {}
        """ :type : dict[str, Port]  """
        self.properties = {}
        """ :type : dict[str, str]  """

    def get_clusters(self):
        """
        Provides the list of clusters in the particular service
        :return: The list of Cluster objects
        :rtype: list[Cluster]
        """
        return self.cluster_id_cluster_map.values()

    def add_cluster(self, cluster):
        """
        Adds a cluster to the service
        :param Cluster cluster: the cluster to be added
        :return: void
        """
        self.cluster_id_cluster_map[cluster.cluster_id] = cluster

    def remove_cluster(self, cluster_id):
        if cluster_id in self.cluster_id_cluster_map:
            self.cluster_id_cluster_map.pop(cluster_id)

    def cluster_exists(self, cluster_id):
        """
        Checks if the cluster with the given cluster id exists for ther service
        :param str cluster_id:
        :return: True if the cluster for the given cluster id exists, False if otherwise
        :rtype: bool
        """
        return cluster_id in self.cluster_id_cluster_map

    def get_cluster(self, cluster_id):
        """
        Provides the Cluster information for the provided cluster id
        :param str cluster_id: the cluster id to search for
        :return: Cluster object for the given cluster id, None if the cluster id is invalid
        :rtype: Cluster
        """
        if cluster_id in self.cluster_id_cluster_map:
            return self.cluster_id_cluster_map[cluster_id]

        return None

    def get_ports(self):
        """
        Returns the list of ports in the particular service
        :return: The list of Port object
        :rtype: list[Port]
        """
        return self.port_map.values()

    def get_port(self, proxy_port):
        """
        Provides the port information for the provided proxy port
        :param str proxy_port:
        :return: Port object for the provided port, None if port is invalid
        :rtype: Port
        """
        if proxy_port in self.port_map:
            return self.port_map[proxy_port]

        return None

    def add_port(self, port):
        self.port_map[port.proxy] = port

    def add_ports(self, ports):
        for port in ports:
            self.add_port(port)


class Cluster:
    """
    Represents a cluster for a service
    """

    def __init__(self, service_name="", cluster_id="", deployment_policy_name="", autoscale_policy_name=""):
        self.service_name = service_name
        """ :type : str  """
        self.cluster_id = cluster_id
        """ :type : str  """
        self.deployment_policy_name = deployment_policy_name
        """ :type : str  """
        self.autoscale_policy_name = autoscale_policy_name
        """ :type : str  """
        self.hostnames = []
        """ :type : list[str]  """
        self.member_map = {}
        """ :type : dict[str, Member]  """

        self.tenant_range = None
        """ :type : str  """
        self.is_lb_cluster = False
        """ :type : bool  """
        self.is_kubernetes_cluster = False
        """ :type : bool  """
        # self.status = None
        # """ :type : str  """
        self.load_balancer_algorithm_name = None
        """ :type : str  """
        self.properties = {}
        """ :type : dict[str, str]  """
        self.member_list_json = None
        """ :type : str  """
        self.app_id = ""
        """ :type : str """
        self.kubernetesService_map = {}
        """ :type : dict[str, KubernetesService]  """
        # Not relevant to cartridge agent
        # self.instance_id_instance_context_map = {}
        # """ :type : dict[str, ClusterInstance] """

    def add_hostname(self, hostname):
        self.hostnames.append(hostname)

    def set_tenant_range(self, tenant_range):
        Cluster.validate_tenant_range(tenant_range)
        self.tenant_range = tenant_range

    def get_members(self):
        """
        Provides the list of member information in the cluster
        :return: The list of Member object
        :rtype: list[Member]
        """
        return self.member_map.values()

    def get_kubernetesServices(self):
        """
        Provides the list of kubernetes Services in the cluster
        :return: The list of KubernetesService object
        :rtype: list[KubernetesService]
        """
        return self.kubernetesService_map.values()

    def add_kubernetesService(self, kubernetesService):
        self.kubernetesService_map[kubernetesService.id] = kubernetesService

    def add_member(self, member):
        self.member_map[member.member_id] = member

    def remove_member(self, member_id):
        if self.member_exists(member_id):
            self.member_map.pop(member_id)

    def get_member(self, member_id):
        """
        Provides the member information for the provided member id
        :param str member_id:
        :return: Member object for the provided member id, None if member id is invalid
        :rtype: Member
        """
        if self.member_exists(member_id):
            return self.member_map[member_id]

        return None

    def member_exists(self, member_id):
        """
        Checks if the member for the provided member id exists in this cluster
        :param str member_id: member id to be searched
        :return: True if the member exists, False if otherwise
        :rtype: bool
        """
        return member_id in self.member_map

    def __str__(self):
        return "Cluster [serviceName=" + self.service_name + ", clusterId=" + self.cluster_id \
               + ", autoscalePolicyName=" + self.autoscale_policy_name + ", deploymentPolicyName=" \
               + self.deployment_policy_name + ", hostNames=" + self.hostnames + ", tenantRange=" + self.tenant_range \
               + ", isLbCluster=" + self.is_lb_cluster + ", properties=" + self.properties + "]"

    def tenant_id_in_range(self, tenant_id):
        """
        Check whether a given tenant id is in tenant range of the cluster.
        :param str tenant_id: tenant id to be checked
        :return: True if the tenant id is in tenant id range, False if otherwise
        :rtype: bool
        """
        if self.tenant_range is None:
            return False

        if self.tenant_range == "*":
            return True
        else:
            arr = self.tenant_range.split(constants.TENANT_RANGE_DELIMITER)
            tenant_start = int(arr[0])
            if tenant_start <= tenant_id:
                tenant_end = arr[1]
                if tenant_end == "*":
                    return True
                else:
                    if tenant_id <= int(tenant_end):
                        return True

        return False

    @staticmethod
    def validate_tenant_range(tenant_range):
        """
        Validates the tenant range to be either '*' or a delimeted range of numbers
        :param str tenant_range: The tenant range string to be validated
        :return: void if the provided tenant range is valid, RuntimeError if otherwise
        :exception: RuntimeError if the tenant range is invalid
        """
        valid = False
        if tenant_range == "*":
            valid = True
        else:
            arr = tenant_range.split(constants.TENANT_RANGE_DELIMITER)
            if len(arr) == 2:
                if arr[0].isdigit() and arr[1].isdigit():
                    valid = True
                elif arr[0].isdigit() and arr[1] == "*":
                    valid = True

        if not valid:
            raise RuntimeError("Tenant range %r is not valid" % tenant_range)


class Member:
    """
    Represents a member on a particular cluster
    """

    def __init__(self, service_name="", cluster_id="", network_partition_id="", partition_id="", member_id="",
                 cluster_instance_id=""):
        self.service_name = service_name
        """ :type : str  """
        self.cluster_id = cluster_id
        """ :type : str  """
        self.network_partition_id = network_partition_id
        """ :type : str  """
        self.cluster_instance_id = cluster_instance_id
        """ :type : str  """
        self.partition_id = partition_id
        """ :type : str  """
        self.member_id = member_id
        """ :type : str  """
        self.port_map = {}
        """ :type : dict[str, Port]  """
        self.init_time = None
        """ :type : int """

        self.member_public_ips = None
        """ :type : str  """
        self.member_default_public_ip = None
        """ :type : str """
        self.status = None
        """ :type : str  """
        self.member_private_ips = None
        """ :type : str  """
        self.member_default_private_ip = None
        """ :type : str """
        self.properties = {}
        """ :type : dict[str, str]  """
        self.lb_cluster_id = None
        """ :type : str  """
        self.json_str = None
        """ :type : str  """

    def is_active(self):
        """
        Checks if the member is in active state
        :return: True if active, False if otherwise
        :rtype: bool
        """
        return self.status == MemberStatus.Active

    def get_ports(self):
        """
        Provides the list of the ports in the member
        :return: List of Port objects
        :rtype: list[Port]
        """
        return self.port_map.values()

    def get_port(self, proxy):
        """
        Provides the port information for the given port id
        :param str proxy: The port id
        :return: Port object of the provided port id, None if otherwise
        :rtype: Port
        """
        if proxy in self.port_map:
            return self.port_map[proxy]

        return None

    def add_port(self, port):
        self.port_map[port.proxy] = port

    def add_ports(self, ports):
        for port in ports:
            self.add_port(port)

    def to_json(self):
        return "{memberId: " + self.member_id + ", status: " + self.status + "}"


class KubernetesService:
    """
    Represents a kubernetes service on a particular cluster
    """

    def __init__(self, id, portalIP, protocol, port, containerPort, serviceType, portName):
        self.id = id
        """ :type : str  """
        self.portalIP = portalIP
        """ :type : str  """
        self.protocol = protocol
        """ :type : str  """
        self.port = port
        """ :type : str  """
        self.containerPort = containerPort
        """ :type : str  """
        self.serviceType = serviceType
        """ :type : str  """
        self.portName = portName
        """ :type : str  """
        self.publicIPs = []
        """ :type : list[str]  """

    def add_public_ips(self, public_ip):
        self.publicIPs.append(public_ip)


class Port:
    """
    Represents a port on a particular member
    """

    def __init__(self, protocol, value, proxy):
        self.protocol = protocol
        """ :type : str  """
        self.value = value
        """ :type : str  """
        self.proxy = proxy
        """ :type : str  """

    def __str__(self):
        return "Port [protocol=%r, value=%r proxy=%r]" % (self.protocol, self.value, self.proxy)


class ServiceType:
    """
    ServiceType enum
    """
    SingleTenant = 1
    MultiTenant = 2


class ClusterStatus:
    """
    ClusterStatus enum
    """
    Created = 1
    In_Maintenance = 2
    Removed = 3


class MemberStatus:
    """
    MemberStatus enum
    """
    Created = "Created"
    Initialized = "Initialized"
    Starting = "Starting"
    Active = "Active"
    In_Maintenance = "In_Maintenance"
    ReadyToShutDown = "ReadyToShutDown"
    Suspended = "Suspended"
    Terminated = "Terminated"


class TopologyContext:
    """
    Handles and maintains a model of the topology provided by the Cloud Controller
    """
    topology = Topology()

    @staticmethod
    def get_topology():
        if TopologyContext.topology is None:
            TopologyContext.topology = Topology()
        return TopologyContext.topology

    @staticmethod
    def update(topology):
        TopologyContext.topology = topology
        TopologyContext.topology.initialized = True


class Tenant:
    """
    Object type representing the tenant details of a single tenant
    """

    def __init__(self, tenant_id, tenant_domain):
        self.tenant_id = tenant_id
        """ :type : int """
        self.tenant_domain = tenant_domain
        """ :type : str """
        self.service_name_subscription_map = {}
        """ :type : dict[str, Subscription] """

    def get_subscription(self, service_name):
        """
        Returns the Subscription object related to the provided service name
        :param str service_name: service name to be retrieved
        :return: Subscription of the service or None if the service name doesn't exist
        :rtype: Subscription
        """
        if service_name in self.service_name_subscription_map:
            return self.service_name_subscription_map[service_name]

        return None

    def is_subscribed(self, service_name):
        """
        Checks if the given service name has a subscription from this tenant
        :param str service_name: name of the service to check
        :return: True if the tenant is subscribed to the given service name, False if not
        :rtype: bool
        """
        return service_name in self.service_name_subscription_map

    def add_subscription(self, subscription):
        """
        Adds a subscription information entry on the subscription list for this tenant
        :param Subscription subscription: Subscription information to be added
        :return: void
        :rtype: void
        """
        self.service_name_subscription_map[subscription.service_name] = subscription

    def remove_subscription(self, service_name):
        """
        Removes the specified subscription details from the subscription list
        :param str service_name: The service name of the subscription to be removed
        :return: void
        :rtype: void
        """
        if service_name in self.service_name_subscription_map:
            self.service_name_subscription_map.pop(service_name)


class Subscription:
    """
    Subscription information of a particular subscription to a service
    """

    def __init__(self, service_name, cluster_ids):
        self.service_name = service_name
        """ :type : str """
        self.cluster_ids = cluster_ids
        """ :type : list[str]  """
        self.subscription_domain_map = {}
        """ :type : dict[str, SubscriptionDomain]  """

    def add_subscription_domain(self, domain_name, application_context):
        """
        Adds a subscription domain
        :param str domain_name:
        :param str application_context:
        :return: void
        :rtype: void
        """
        self.subscription_domain_map[domain_name] = SubscriptionDomain(domain_name, application_context)

    def remove_subscription_domain(self, domain_name):
        """
        Removes the subscription domain of the specified domain name
        :param str domain_name:
        :return: void
        :rtype: void
        """
        if domain_name in self.subscription_domain_map:
            self.subscription_domain_map.pop(domain_name)

    def subscription_domain_exists(self, domain_name):
        """
        Returns the SubscriptionDomain information of the specified domain name
        :param str domain_name:
        :return: SubscriptionDomain
        :rtype: SubscriptionDomain
        """
        return domain_name in self.subscription_domain_map

    def get_subscription_domains(self):
        """
        Returns the list of subscription domains of this subscription
        :return: List of SubscriptionDomain objects
        :rtype: list[SubscriptionDomain]
        """
        return self.subscription_domain_map.values()


class SubscriptionDomain:
    """
    Represents a Subscription Domain
    """

    def __init__(self, domain_name, application_context):
        self.domain_name = domain_name
        """ :type : str  """
        self.application_context = application_context
        """ :type : str  """


class TenantContext:
    """
    Handles and maintains a model of all the information related to tenants within this instance
    """
    tenants = {}
    initialized = False
    tenant_domains = {"carbon.super": Tenant(-1234, "carbon.super")}

    @staticmethod
    def add_tenant(tenant):
        TenantContext.tenants[tenant.tenant_id] = tenant
        TenantContext.tenant_domains[tenant.tenant_domain] = tenant

    @staticmethod
    def remove_tenant(tenant_id):
        if tenant_id in TenantContext.tenants:
            tenant = TenantContext.get_tenant(tenant_id)
            TenantContext.tenants.pop(tenant.tenant_id)
            TenantContext.tenant_domains.pop(tenant.tenant_domain)

    @staticmethod
    def update(tenants):
        for tenant in tenants:
            TenantContext.add_tenant(tenant)

    @staticmethod
    def get_tenant(tenant_id):
        """
        Gets the Tenant object of the provided tenant ID
        :param int tenant_id:
        :return: Tenant object of the provided tenant ID
        :rtype: Tenant
        """
        if tenant_id in TenantContext.tenants:
            return TenantContext.tenants[tenant_id]

        return None

    @staticmethod
    def get_tenant_by_domain(tenant_domain):
        """
        Gets the Tenant object of the provided tenant domain
        :param str tenant_domain:
        :return: Tenant object of the provided tenant domain
        :rtype: str
        """
        if tenant_domain in TenantContext.tenant_domains:
            return TenantContext.tenant_domains[tenant_domain]

        return None