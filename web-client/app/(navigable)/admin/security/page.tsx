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

import { useCallback, useContext, useMemo } from "react"
import { ShieldCheckIcon, UserIcon, UsersIcon } from '@heroicons/react/24/outline'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"
import { FormProvider } from "react-hook-form"
import { columns as groupColumns } from "@/app/ui/tables/group-columns"
import { columns as userColumns } from "@/app/ui/tables/user-columns"
import IPage from "@/app/model/IPage"
import Group from "@/app/model/Group"
import User from "@/app/model/User"
import DataTable from "@/app/ui/data-table/data-table"
import GroupDetails from "@/app/ui/details/group-details"
import UserDetails from "@/app/ui/details/user-details"
import { GroupFieldValues, GroupSchema } from "@/app/ui/validators/group"
import { UserFieldValues, UserSchema } from "@/app/ui/validators/user"
import Authority from "@/app/model/Authority"
import { AuthoritiesFieldValues } from "@/app/ui/validators/authority"
import {
  CREATE_GROUP,
  CREATE_USER,
  DELETE_GROUP,
  DELETE_USER,
  READ_GROUPS,
  READ_USERS,
  UPDATE_GROUP,
  UPDATE_USER,
  ADD_GROUP_MEMBER,
  REMOVE_GROUP_MEMBER,
} from "@/lib/graphql-queries"
import usePageLogic from "@/hooks/use-page-logic"
import { GroupInput, TrackedEntityQueryFilter, UserInput } from "@/app/model/schema"
import { useMutation } from "@apollo/client"
import LinkableEntityTableFilter from "@/app/ui/filter/linkable-entity-table-filter"
import { GlobalContext, SecurityPageTabState } from "@/lib/context"

function createAuthoritiesFieldValues(authorities?: Authority[]) {
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

function createGroupFieldValues(group?: Group) : GroupFieldValues {
  return {
    groupname: group?.groupname ?? '',
    ...createAuthoritiesFieldValues(group?.authorities)
  }
}

function createUserFieldValues(user?: User) : UserFieldValues {
  return {
    username: user?.username ?? '',
    password: user?.password ?? '',
    firstName: user?.firstName ?? '',
    lastName: user?.lastName ?? '',
    email: user?.email ?? '',
    country: user?.country ?? '',
    notes: user?.notes ?? '',
    ...createAuthoritiesFieldValues(user?.authorities)
  }
}

function createAuthorities(formValue: AuthoritiesFieldValues) {
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

function createGroupInput(fieldValues: GroupFieldValues, id?: string) : GroupInput {
  return {
    id,
    groupname: fieldValues.groupname,
    authorities: createAuthorities(fieldValues),
  }
}

function createUserInput(fieldValues: UserFieldValues, id?: string) : UserInput {
  return {
    id,
    username: fieldValues.username,
    password: fieldValues.password || null,
    firstName: fieldValues.firstName,
    lastName: fieldValues.lastName,
    email: fieldValues.email,
    country: fieldValues.country || null,
    notes: fieldValues.notes || null,
    authorities: createAuthorities(fieldValues),
  }
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
    totalElements: users.length,
    isFirst: true,
    isLast: true,
    hasNext: false,
    hasPrevious: false,
  }
}

export default function Security() {
  const {
    loading: userLoading,
    page: userPage,
    selectedRecord: selectedUser,
    handleRowSelectionChange: handleUserSelectionChange,
    state: userState,
    setMode: setUserMode,
    form: userForm,
    handleFormAction: handleUserFormAction,
    refetch: refetchUsers,
  } = usePageLogic<User, UserFieldValues, UserInput, TrackedEntityQueryFilter>({
    recordKind: "User",
    schema: UserSchema,
    manualPagination: false,
    manualSorting: false,
    readQuery: READ_USERS,
    createMutation: CREATE_USER,
    updateMutation: UPDATE_USER,
    deleteMutation: DELETE_USER,
    createFieldValues: createUserFieldValues,
    createInput: createUserInput,
  })
  const {
    loading: groupLoading,
    page: groupPage,
    selectedRecord: selectedGroup,
    handleRowSelectionChange: handleGroupSelectionChange,
    state: groupState,
    setMode: setGroupMode,
    form: groupForm,
    handleFormAction: handleGroupFormAction,
    refetch: refetchGroups
  } = usePageLogic<Group, GroupFieldValues, GroupInput, TrackedEntityQueryFilter>({
    recordKind: "Group",
    schema: GroupSchema,
    manualPagination: false,
    manualSorting: false,
    readQuery: READ_GROUPS,
    createMutation: CREATE_GROUP,
    updateMutation: UPDATE_GROUP,
    deleteMutation: DELETE_GROUP,
    createFieldValues: createGroupFieldValues,
    createInput: createGroupInput,
  })

  const {queries, setActiveSecurityPageTab, setShowUsersOrMembers} = useContext(GlobalContext)
  const groupQueryState = queries["Group"]
  const userQueryState = queries["User"]
  const activeTab = groupQueryState?.activeTab ?? "groups"
  const showUsersOrMembers = userQueryState?.showUsersOrMembers ?? "users"

  const onActiveTabChange = useCallback((activeTab: string) => {
    setActiveSecurityPageTab(activeTab as SecurityPageTabState)
  }, [setActiveSecurityPageTab])

  // const {storeAppState} = useContext(GlobalContext)
  // useEffect(() => {return () => storeAppState()}, [])

  const [addGroupMemberOp/*, addGroupMemberResult*/] = useMutation(ADD_GROUP_MEMBER, { refetchQueries: [READ_GROUPS] })
  const [removeGroupMemberOp/*, removeGroupMemberResult*/] = useMutation(REMOVE_GROUP_MEMBER, { refetchQueries: [READ_GROUPS] })

  const userPageToShow = useMemo(() => {
      return showUsersOrMembers == "users" ? userPage : createDummyPage(selectedGroup?.members)
    }, [showUsersOrMembers, userPage, selectedGroup])

  const handleUserFormActionEx = useCallback((command: string, userFieldValues?: UserFieldValues) => {
    switch (command) {
      case "new":
      case "create":
      case "update":
      case "delete":
      case "reset":
        handleUserFormAction(command, userFieldValues)
        break
      case "add":
        if (selectedGroup && selectedUser) {
          addGroupMemberOp({
            variables: {
              groupId: selectedGroup.id,
              userId: selectedUser.id,
            }
          })
        }
        break
      case "remove":
        if (selectedGroup && selectedUser) {
          removeGroupMemberOp({
            variables: {
              groupId: selectedGroup.id,
              userId: selectedUser.id,
            }
          })
        }
        break
    }
  }, [handleUserFormAction, selectedGroup, selectedUser, addGroupMemberOp, removeGroupMemberOp])

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ShieldCheckIcon className="w-8 h-8" />
        &nbsp;
        <h1>Security</h1>
      </div>
      <Tabs defaultValue="groups" value={activeTab} onValueChange={onActiveTabChange}>
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
            page={groupPage}
            state={groupState}
            loading={groupLoading}
            filterComponent={LinkableEntityTableFilter}
            manualPagination={false}
            manualSorting={false}
            onRowSelectionChange={handleGroupSelectionChange}
            refetch={refetchGroups}
            loadingPathWithSearchParams={false}
          />
          <FormProvider {...groupForm}>
            <GroupDetails
              record={selectedGroup}
              state={groupState}
              setMode={setGroupMode}
              onFormAction={handleGroupFormAction}
            />
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
              page={userPageToShow}
              state={userState}
              loading={userLoading}
              filterComponent={LinkableEntityTableFilter}
              manualPagination={false}
              manualSorting={false}
              onRowSelectionChange={handleUserSelectionChange}
              refetch={refetchUsers}
            loadingPathWithSearchParams={false}
            />
          </div>
          <FormProvider {...userForm}>
            <UserDetails
              user={selectedUser}
              group={selectedGroup}
              state={userState}
              setMode={setUserMode}
              onFormAction={handleUserFormActionEx}
            />
          </FormProvider>
        </TabsContent>
      </Tabs>
    </main>
  )
}