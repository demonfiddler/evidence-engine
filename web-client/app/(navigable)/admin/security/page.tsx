/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

'use client'

// import type { Metadata } from "next"
import { useCallback, useContext, useMemo, useState } from "react"
import { ShieldCheckIcon, UserIcon, UsersIcon } from '@heroicons/react/24/outline'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import { useImmerReducer } from "use-immer"
import { useForm, FormProvider } from "react-hook-form"

import { columns as groupColumns, columnVisibility as groupColumnVisibility } from "@/app/ui/tables/group-columns"
import { columns as userColumns, columnVisibility as userColumnVisibility } from "@/app/ui/tables/user-columns"
import rawGroupPage from "@/data/groups.json" assert {type: 'json'}
import rawUserPage from "@/data/users.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Group from "@/app/model/Group";
import User from "@/app/model/User";
import { SelectedRecordsContext } from "@/lib/context";
import DataTable from "@/app/ui/data-table/data-table"
import GroupDetails from "@/app/ui/details/group-details"
import UserDetails from "@/app/ui/details/user-details";
import { GroupFormFields, GroupSchema } from "@/app/ui/validators/group";
import { UserFormFields, UserSchema } from "@/app/ui/validators/user";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema"
import Authority from "@/app/model/Authority"
import { AuthoritiesFormFields } from "@/app/ui/validators/authority"
import { MutationAction, FormAction, SecurityFormAction } from "@/lib/utils"

// export const metadata: Metadata = {
//   title: "Users & Groups",
//   description: "System user and group administration",
// };
function copyAuthoritiesFromForm(formValue: AuthoritiesFormFields) {
  const authorities: Authority[] = []
  if (formValue.adm)
    authorities.push("ADM")
  if (formValue.cre)
    authorities.push("CRE")
  if (formValue.del)
    authorities.push("DEL")
  if (formValue.lnk)
    authorities.push("LNK")
  if (formValue.rea)
    authorities.push("REA")
  if (formValue.upd)
    authorities.push("UPD")
  if (formValue.upl)
    authorities.push("UPL")
  return authorities
}

function copyAuthoritiesToForm(authorities?: Authority[]) {
  return {
    adm: authorities?.includes("ADM") ?? false,
    cre: authorities?.includes("CRE") ?? false,
    del: authorities?.includes("DEL") ?? false,
    lnk: authorities?.includes("LNK") ?? false,
    rea: authorities?.includes("REA") ?? false,
    upd: authorities?.includes("UPD") ?? false,
    upl: authorities?.includes("UPL") ?? false,
  }
}

function copyGroupToForm(group?: Group) {
  return {
    groupname: group?.groupname ?? '',
    ...copyAuthoritiesToForm(group?.authorities)
  }
}

function copyUserToForm(user?: User) {
  return {
    username: user?.username ?? '',
    password: user?.password ?? '',
    firstName: user?.firstName ?? '',
    lastName: user?.lastName ?? '',
    email: user?.email ?? '',
    country: user?.country ?? '',
    // notes: user?.notes ?? '',
    ...copyAuthoritiesToForm(user?.authorities)
  }
}

function copyGroupFromForm(group: Group, formValue: GroupFormFields) {
  group.groupname = formValue.groupname,
  group.authorities = copyAuthoritiesFromForm(formValue)
}

function copyUserFromForm(user: User, formValue: UserFormFields) {
  user.username = formValue.username
  user.password = formValue.password
  user.firstName = formValue.firstName ?? null
  user.lastName = formValue.lastName ?? null
  user.email = formValue.email ?? null
  user.country = formValue.country ?? null
  // user.notes = formValue.notes ?? null
  user.authorities = copyAuthoritiesFromForm(formValue)
}

export default function Security() {
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedUserId, setSelectedUserId] = useState<string | undefined>(selectedRecordsContext.User?.id)
  const [selectedGroupId, setSelectedGroupId] = useState<string | undefined>(selectedRecordsContext.Group?.id)
  const userPageReducer = useCallback((draft: IPage<User>, action: MutationAction<FormAction, UserFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedUserId)
    switch (action.command) {
      case "create":
        const user: User = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedUserId(user.id)
        copyUserFromForm(user, action.value)
        draft.content.push(user)
        break
      case "update":
        if (idx != -1) {
          const user = draft.content[idx]
          copyUserFromForm(user, action.value)
          draft.content.splice(idx, 1, user)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
    }
  }, [selectedUserId, setSelectedUserId])
  const [userPage, userPageDispatch] = useImmerReducer(userPageReducer, rawUserPage as unknown as IPage<User>)
  const groupPageReducer = useCallback((draft: IPage<Group>, action: MutationAction<SecurityFormAction, GroupFormFields>) => {
    const idx = draft.content.findIndex(c => c.id == selectedGroupId)
    switch (action.command) {
      case "create":
        const group: Group = {
          id: (Math.max(...draft.content.map(c => parseInt(c.id ?? ''))) + 1).toString(),
          status: "Draft"
        }
        setSelectedGroupId(group.id)
        copyGroupFromForm(group, action.value)
        draft.content.push(group)
        break
      case "update":
        if (idx != -1) {
          const group = draft.content[idx]
          copyGroupFromForm(group, action.value)
          draft.content.splice(idx, 1, group)
        }
        break
      case "delete":
        // N.B. This is a hard delete that physically deletes the record. The server implements a soft delete.
        if (idx != -1)
          draft.content.splice(idx, 1)
        break
      case "add": {
        // Can't use outer const selectedUser because it hasn#'t been initialised yet
        const selectedUser = getSelectedUser(selectedUserId)
        if (idx != -1 && selectedUser) {
          const draftGroup = draft.content[idx]
          if (draftGroup.members) {
            // Sanity check to avoid adding a duplicate member
            const draftUser = draftGroup.members.content.find(user => user.id == selectedUserId)
            if (!draftUser) {
              // FIXME: some fields may be incorrect
              draftGroup.members.numberOfElements++
              // draftGroup.members.totalElements++
              draftGroup.members.hasContent = true
              draftGroup.members.isEmpty = false
              draftGroup.members.content.push(selectedUser)
            }
          }
        }}
        break
      case "remove": {
        // Can't use outer const selectedUser because it hasn#'t been initialised yet
        const selectedUser = getSelectedUser(selectedUserId)
        if (idx != -1 && selectedUser) {
          const draftGroup = draft.content[idx]
          if (draftGroup.members) {
            // Sanity check to avoid removing a nonexistent member
            const draftUserIdx = draftGroup.members.content.findIndex(user => user.id == selectedUserId)
            console.log(`draftUserIdx = ${draftUserIdx}`)
            if (draftUserIdx != -1) {
              // FIXME: some fields may be incorrect
              draftGroup.members.numberOfElements--
              // draftGroup.members.totalElements--
              draftGroup.members.hasContent = draftGroup.members.numberOfElements != 0
              draftGroup.members.isEmpty = !draftGroup.members.hasContent
              draftGroup.members.content.splice(draftUserIdx, 1)
              setSelectedUserId(undefined)
            }
          }
        }}
        break
    }
  }, [selectedUserId, setSelectedUserId, getSelectedUser, selectedGroupId, setSelectedGroupId])
  const [groupPage, groupPageDispatch] = useImmerReducer(groupPageReducer, rawGroupPage as unknown as IPage<Group>)
  const selectedUser = getSelectedUser(selectedUserId)
  const selectedGroup = getSelectedGroup(selectedGroupId)
  const [showUsersOrMembers, setShowUsersOrMembers] = useState("users")
  const origUserFormValue = useMemo(() => copyUserToForm(selectedUser), [selectedUser])
  const userForm = useForm<UserFormFields>({
    resolver: standardSchemaResolver(UserSchema),
    mode: "onChange",
    values: origUserFormValue
  })
  const origGroupFormValue = useMemo(() => copyGroupToForm(selectedGroup), [selectedGroup])
  const groupForm = useForm<GroupFormFields>({
    resolver: standardSchemaResolver(GroupSchema),
    mode: "onChange",
    values: origGroupFormValue
  })

  const handleGroupFormAction = useCallback((command: SecurityFormAction, formValue: GroupFormFields) => {
    switch (command) {
      case "create":
      case "update":
      case "delete":
        groupPageDispatch({ command: command, value: formValue })
        break
      case "reset":
        groupForm.reset(origGroupFormValue)
        break
      case "add":
      case "remove":
        throw new Error(`Unsupported command: ${command}`)
    }
  }, [groupForm, groupPageDispatch, selectedGroup])

  const handleUserFormAction = useCallback((command: SecurityFormAction, formValue: UserFormFields) => {
    switch (command) {
      case "create":
      case "update":
      case "delete":
        userPageDispatch({ command: command, value: formValue })
        break
      case "reset":
        userForm.reset(origUserFormValue)
        break
      case "add":
      case "remove":
        // NOTE: we're only passing formValue to keep the compiler happy - the value isn't used.
        groupPageDispatch({ command: command, value: groupForm.getValues() })
        break
    }
  }, [userForm, userPageDispatch, selectedUser])

  function getSelectedGroup(id?: string) {
    return groupPage.content.find(r => r.id == id)
  }

  function getSelectedUser(id?: string) {
    return userPage.content.find(r => r.id == id)
  }

  const handleGroupSelectionChange = useCallback((id?: string) => {
    setSelectedGroupId(id)
    setSelectedUserId(undefined)
    const group = getSelectedGroup(id)
    groupForm.reset(copyGroupToForm(group))
  }, [setSelectedGroupId, setSelectedUserId, getSelectedGroup, groupForm])

  const handleUserSelectionChange = useCallback((id?: string) => {
    setSelectedUserId(id)
    const user = getSelectedUser(id)
    userForm.reset(copyUserToForm(user))
  }, [setSelectedUserId, getSelectedUser, userForm])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ShieldCheckIcon className="w-8 h-8" />
        &nbsp;
        <h1>Security</h1>
      </div>
      <Tabs defaultValue="groups">
        <TabsList>
          <TabsTrigger value="groups">Groups</TabsTrigger>
          <TabsTrigger value="users">Users</TabsTrigger>
        </TabsList>
        <TabsContent className="grid grid-cols-1 gap-4" value="groups">
          <div className="flex flex-row items-center">
            <UsersIcon className="w-6 h-6" />
            &nbsp;
            <h2>Groups</h2>
          </div>
          <DataTable<Group, unknown>
            className="size-fit min-w-[700px]"
            recordKind="Group"
            defaultColumns={groupColumns}
            defaultColumnVisibility={groupColumnVisibility}
            page={groupPage}
            onRowSelectionChange={handleGroupSelectionChange}
          />
          <FormProvider {...groupForm}>
            <GroupDetails record={selectedGroup} onFormAction={handleGroupFormAction} />
          </FormProvider>
        </TabsContent>
        <TabsContent className="grid grid-cols-1 gap-4" value="users">
          <div className="size-fit">
            <div className="flex flex-row items-center">
              <UserIcon className="w-6 h-6" />
              &nbsp;
              <h2>Users</h2>
              <div className="flex grow justify-end">
                Show:
                &nbsp;
                <RadioGroup
                  className="flex flex-row"
                  value={showUsersOrMembers}
                  onValueChange={setShowUsersOrMembers}>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem id="users" value="users" />
                    <Label htmlFor="users">Users</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem id="members" value="members" disabled={!selectedGroup} />
                    <Label htmlFor="members">
                      {
                        selectedGroup
                          ? `Members of Group '${selectedGroup?.groupname}'`
                          : "Group members: select a group"
                      }
                    </Label>
                  </div>
                </RadioGroup>
              </div>
            </div>
            <DataTable<User, unknown>
              className="size-fit"
              recordKind="User"
              defaultColumns={userColumns}
              defaultColumnVisibility={userColumnVisibility}
              page={showUsersOrMembers == "users" ? userPage : selectedGroup?.members}
              onRowSelectionChange={handleUserSelectionChange}
            />
          </div>
          <FormProvider {...userForm}>
            <UserDetails
              user={selectedUser}
              group={selectedGroup}
              showUsersOrMembers={showUsersOrMembers}
              onFormAction={handleUserFormAction}
            />
          </FormProvider>
        </TabsContent>
      </Tabs>
    </main>
  );
}