#!/usr/bin/env python
"""
creating ldif from jinja2 template
"""
import os

import click
import logging
import uuid
from jinja2 import Environment, BaseLoader

logging.basicConfig(level=logging.DEBUG)

logger = logging.getLogger(__name__)


@click.group()
def cli():
    """
    top level command line tool
    """
    pass


@cli.command('global-tasks-succeeded')
@click.argument('number-of-entries', type=int)
@click.argument('data-center', type=str)
def global_tasks_succeeded(number_of_entries, data_center):
    """
    Creates global task entries with unique UUID in succeeded status
    number_of_entries: integer with amount of entries to generate
    data_center: which data center to create the entry for: i.e. localhost, prd, dfw, etc.
    """
    jinja_temp = """{% for uuid in uuids -%}
dn: globalTaskUUID={{ uuid }},dc=globalTasks,dc=cloudatlas
changetype: add
objectClass: top
objectClass: globalTask
apiVersion: 2
taskParameter: DNToDelete=tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
taskParameter: entityTypeToDelete=Tenant
taskOwner: tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
executionDatacenter: {{ datacenter }}
inTransaction: true
executionStatus: succeeded
taskTypeDN: taskTypeName=DeleteOwnerTask,dc=globalTasktypes,dc=globalTasks,dc=cloudatlas
globalTaskUUID: {{ uuid }}
executionHost: shared0-{{ datacenter }}
taskResult: [{"resultCode":0,"result":"success","diagnosticMessage":"This operation will be processed as part of transaction 3177","matchedDN":"extAttributes=gid,tenantKey=core/test1na46/00DRO0000009Bx42AE,cloudName=core,dc=clouds,dc=cloudatlas"},{"resultCode":0,"result":"success","diagnosticMessage":"This operation will be processed as part of transaction 3177","matchedDN":"tenantKey=core/test1na46/00DRO0000009Bx42AE,cloudName=core,dc=clouds,dc=cloudatlas"}]
{% endfor %}
"""
    uuids = []
    for index in range(number_of_entries):
        uuids.append(str(uuid.uuid4()))

    template = Environment(loader=BaseLoader).from_string(jinja_temp)
    succeeded_tasks_file = f"{os.getcwd()}/succeeded-globaltasks.ldif"
    with open(succeeded_tasks_file, "w") as f:
        print(f"Rendering template to  file {succeeded_tasks_file}..")
        f.write(f"{template.render(uuids=uuids, datacenter=data_center)}")


@cli.command('global-tasks-scheduler')
@click.argument('number-of-entries', type=int)
@click.argument('data_center', type=str)
def global_tasks_scheduler(number_of_entries, data_center):
    """
    Creates global task entries with unique UUID in created status
    number_of_entries: integer with amount of entries to generate
    data_center: which data center to create the entry for: i.e. localhost, prd, dfw, etc.
    """
    jinja_temp = """{% for uuid in uuids -%}
dn: globalTaskUUID={{ uuid }},dc=globalTasks,dc=cloudatlas
objectClass: top
objectClass: globalTask
apiVersion: 2
taskParameter: DNToDelete=tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
taskParameter: entityTypeToDelete=Tenant
taskOwner: tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
executionDatacenter: {{ datacenter }}
inTransaction: true
executionStatus: created
taskTypeDN: taskTypeName=DeleteOwnerTask,dc=globalTasktypes,dc=globalTasks,dc=cloudatlas
globalTaskUUID: {{ uuid }}

{% endfor %}
"""

    uuids = []
    for index in range(number_of_entries):
        uuids.append(str(uuid.uuid4()))

    template = Environment(loader=BaseLoader).from_string(jinja_temp)
    created_tasks_file = f"{os.getcwd()}/created-globaltasks-scheduler.ldif"
    with open(created_tasks_file, "w") as f:
        print(f"Rendering template to  file {created_tasks_file}..")
        f.write(f"{template.render(uuids=uuids, datacenter=data_center)}")


@cli.command('global-tasks-executor')
@click.argument('number-of-entries', type=int)
@click.argument('data_center', type=str)
def global_tasks_executor(number_of_entries, data_center):
    """
    Creates global task entries with unique UUID in created status
    number_of_entries: integer with amount of entries to generate
    data_center: which data center to create the entry for: i.e. localhost, prd, dfw, etc.
    """
    jinja_temp = """{% for uuid in uuids -%}
dn: globalTaskUUID={{ uuid }},dc=globalTasks,dc=cloudatlas
objectClass: top
objectClass: globalTask
apiVersion: 2
taskParameter: DNToDelete=tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
taskParameter: entityTypeToDelete=Tenant
taskOwner: tenantKey=core/stmfbna44ice1/00DRM000000JSLp2AO,cloudName=core,dc=clouds,dc=cloudatlas
executionDatacenter: {{ datacenter }}
inTransaction: true
executionStatus: created
executionHost: shared0-{{ datacenter }}
taskTypeDN: taskTypeName=DeleteOwnerTask,dc=globalTasktypes,dc=globalTasks,dc=cloudatlas
globalTaskUUID: {{ uuid }}

{% endfor %}
"""

    uuids = []
    for index in range(number_of_entries):
        uuids.append(str(uuid.uuid4()))

    template = Environment(loader=BaseLoader).from_string(jinja_temp)
    created_tasks_file = f"{os.getcwd()}/created-globaltasks-executor.ldif"
    with open(created_tasks_file, "w") as f:
        print(f"Rendering template to  file {created_tasks_file}..")
        f.write(f"{template.render(uuids=uuids, datacenter=data_center)}")


if __name__ == '__main__':
    cli()
