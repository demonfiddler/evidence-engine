# ----------------------------------------------------------------------------------------------------------------------
#  Evidence Engine: A system for managing evidence on arbitrary scientific topics.
#  Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
#  Copyright © 2024-26 Adrian Price. All rights reserved.
#
#  This file is part of Evidence Engine.
#
#  Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
#  GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
#  or (at your option) any later version.
#
#  Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
#  If not, see <https://www.gnu.org/licenses/>. 
# ----------------------------------------------------------------------------------------------------------------------

from __future__ import annotations
from pydantic import BaseModel, Field
from typing import Annotated

from ee_ai_service.models.enums.severity_kind import SeverityKind
from ee_ai_service.models.tracked_entity import TrackedEntity

# An individual field audit entry.
class FieldAuditEntry(BaseModel):
    # The field name.
    fieldName: str
    # A message about the field.
    message: str
    # The message severity level.
    severity: SeverityKind
    # Whether the field meets the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# Holds field group audit information. At least one field in the group must be set.
class FieldGroupAuditEntry(BaseModel):
    # Field group audit entries.
    fields: list[FieldAuditEntry]
    # A message about the field group.
    message: str
    # The message severity level.
    severity: SeverityKind
    # Whether the field group meets the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# An individual field audit entry.
class FieldAudit(BaseModel):
    # The individual field audit entries.
    fields: list[FieldAuditEntry]
    # Field group audit entries.
    groups: list[FieldGroupAuditEntry]
    # Whether the fields meet the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# An individual link audit entry.
class LinkAuditEntry(BaseModel):
    # The linked entity kind.
    linkedEntityKind: str
    # The required minimum number of links.
    min: int
    # The actual number of links.
    actual: int
    # Whether the link meets the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# Holds link group audit information. At least one link in the group must be present.
class LinkGroupAuditEntry(BaseModel):
    # Link group audit entries.
    links: list[LinkAuditEntry]
    # Whether the link group meets the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# Holds aggregated link audit information.
class LinkAudit(BaseModel):
    # The individual link audit entries.
    links: list[LinkAuditEntry]
    # Link group audit entries.
    groups: list[LinkGroupAuditEntry]
    # Whether the links meet the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]

# Holds aggregated audit information for an entity.
class EntityAudit(BaseModel) :
    # The entity to which the audit relates.
    entity: TrackedEntity
    # Field audit information.
    fieldAudit: FieldAudit
    # Link audit information. Only populated if entity is an ILinkableEntity# 
    linkAudit: LinkAudit
    # Whether the entity meets the standard required for publication.
    _pass: Annotated[bool, Field(alias="pass")]
