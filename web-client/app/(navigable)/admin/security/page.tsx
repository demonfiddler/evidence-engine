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

import { useCallback, useMemo, useState } from "react"
import { ShieldCheckIcon, UserIcon, UsersIcon } from '@heroicons/react/24/outline'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import { FormProvider } from "react-hook-form"
import { columns as groupColumns, columnVisibility as groupColumnVisibility } from "@/app/ui/tables/group-columns"
import { columns as userColumns, columnVisibility as userColumnVisibility } from "@/app/ui/tables/user-columns"
import IPage from "@/app/model/IPage";
import Group from "@/app/model/Group";
import User from "@/app/model/User";
import DataTable from "@/app/ui/data-table/data-table"
import GroupDetails from "@/app/ui/details/group-details"
import UserDetails from "@/app/ui/details/user-details";
import { GroupFieldValues, GroupSchema } from "@/app/ui/validators/group";
import { UserFieldValues, UserSchema } from "@/app/ui/validators/user";
import Authority from "@/app/model/Authority"
import { AuthoritiesFieldValues } from "@/app/ui/validators/authority"
import { SecurityFormAction } from "@/lib/utils"
import { QUERY_GROUPS, QUERY_USERS } from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { TrackedEntityQueryFilter } from "@/app/model/schema"

function copyAuthoritiesFromForm(formValue: AuthoritiesFieldValues) {
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

function copyGroupFromForm(group: Group, fieldValues: GroupFieldValues) {
  group.groupname = fieldValues.groupname,
  group.authorities = copyAuthoritiesFromForm(fieldValues)
}

function copyUserFromForm(user: User, fieldValues: UserFieldValues) {
  user.username = fieldValues.username
  user.password = fieldValues.password
  user.firstName = fieldValues.firstName ?? null
  user.lastName = fieldValues.lastName ?? null
  user.email = fieldValues.email ?? null
  user.country = fieldValues.country ?? null
  // user.notes = fieldValues.notes ?? null
  user.authorities = copyAuthoritiesFromForm(fieldValues)
}

function createDummyPage(users?: User[]) : IPage<User> | undefined {
  return users && {
    content: users,
    hasContent: !!users,
    isEmpty: !users,
    number: 0,
    size: 0,
    numberOfElements: users.length,
    totalPages: users.length ? 1 : 0,
    totalElements: users.length.toString(),
    isFirst: true,
    isLast: true,
    hasNext: false,
    hasPrevious: false,
  }
}

export default function Security() {
  const {
    search: userSearch,
    setSearch: setUserSearch,
    pagination: userPagination,
    setPagination: setUserPagination,
    loading: userLoading,
    page: userPage,
    selectedRecord: selectedUser,
    handleRowSelectionChange: handleUserSelectionChange,
    form: userForm,
    handleFormAction: handleUserFormAction,
  } = usePageLogic<User, TrackedEntityQueryFilter, UserFieldValues>({
    recordKind: "User",
    schema: UserSchema,
    listQuery: QUERY_USERS,
    copyToForm: copyUserToForm,
    copyFromForm: copyUserFromForm,
  })
  const {
    search: groupSearch,
    setSearch: setGroupSearch,
    pagination: groupPagination,
    setPagination: setGroupPagination,
    loading: groupLoading,
    page: groupPage,
    selectedRecord: selectedGroup,
    handleRowSelectionChange: handleGroupSelectionChange,
    form: groupForm,
    handleFormAction: handleGroupFormAction,
  } = usePageLogic<Group, TrackedEntityQueryFilter, GroupFieldValues>({
    recordKind: "Group",
    schema: GroupSchema,
    listQuery: QUERY_GROUPS,
    copyToForm: copyGroupToForm,
    copyFromForm: copyGroupFromForm,
  })
  const [showUsersOrMembers, setShowUsersOrMembers] = useState("users")
  const userPageToShow = useMemo(() => {
      return showUsersOrMembers == "users" ? userPage : createDummyPage(selectedGroup?.members)
    }, [showUsersOrMembers, userPage, selectedGroup])
  const handleUserFormActionEx = useCallback((command: SecurityFormAction, userFieldValues: UserFieldValues) => {
    switch (command) {
      case "create":
      case "update":
      case "delete":
      case "reset":
        handleUserFormAction(command, userFieldValues)
        break
      case "add":
      case "remove":
        // NOTE: we're only passing userFieldValues to keep the compiler happy - the value isn't used.
        // OLD: groupPageDispatch({ command: command, value: userFieldValues })
        break
    }
  }, [userForm, /*userPageDispatch,*/ selectedUser])

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
            loading={groupLoading}
            pagination={groupPagination}
            onPaginationChange={setGroupPagination}
            search={groupSearch}
            onSearchChange={setGroupSearch}
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
              page={userPageToShow}
              loading={userLoading}
              pagination={userPagination}
              onPaginationChange={setUserPagination}
              search={userSearch}
              onSearchChange={setUserSearch}
              onRowSelectionChange={handleUserSelectionChange}
            />
          </div>
          <FormProvider {...userForm}>
            <UserDetails
              user={selectedUser}
              group={selectedGroup}
              showUsersOrMembers={showUsersOrMembers}
              onFormAction={handleUserFormActionEx}
            />
          </FormProvider>
        </TabsContent>
      </Tabs>
    </main>
  );
}