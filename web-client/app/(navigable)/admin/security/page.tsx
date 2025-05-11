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
import { useContext, useState } from "react"
import { ShieldCheckIcon, UserIcon, UsersIcon } from '@heroicons/react/24/outline'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import GroupDetails from "@/app/ui/details/group-details"
import DataTable from "@/app/ui/data-table/data-table"

import { columns as groupColumns, columnVisibility as groupColumnVisibility } from "@/app/ui/tables/group-columns"
import { columns as userColumns, columnVisibility as userColumnVisibility } from "@/app/ui/tables/user-columns"
import rawGroupPage from "@/data/groups.json" assert {type: 'json'}
import rawUserPage from "@/data/users.json" assert {type: 'json'}
import IPage from "@/app/model/IPage";
import Group from "@/app/model/Group";
import { SelectedRecordsContext } from "@/lib/context";
import User from "@/app/model/User";
import UserDetails from "@/app/ui/details/user-details";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { Label } from "@/components/ui/label"

// export const metadata: Metadata = {
//   title: "Users & Groups",
//   description: "System user and group administration",
// };

export default function Security() {
  const groupPage = rawGroupPage as unknown as IPage<Group>
  const userPage = rawUserPage as unknown as IPage<User>
  const selectedRecordsContext = useContext(SelectedRecordsContext)
  const [selectedGroup, setSelectedGroup] = useState<Group|undefined>(() => {
    const selectedRecordId = selectedRecordsContext.Group?.id
    return groupPage.content.find(record => record.id == selectedRecordId)
  });
  const [selectedUser, setSelectedUser] = useState<User|undefined>(() => {
    const selectedRecordId = selectedRecordsContext.User?.id
    return userPage.content.find(record => record.id == selectedRecordId)
  });
  const [showUsersOrMembers, setShowUsersOrMembers] = useState("users")

  return (
    <main className="flex flex-col items-start m-4 gap-4">
      <div className="flex flex-row items-center">
        <ShieldCheckIcon className="w-8 h-8"/>
        &nbsp;
        <h1>Security</h1>
      </div>
      <Tabs defaultValue="groups">
        <TabsList>
          <TabsTrigger value="groups">Groups</TabsTrigger>
          <TabsTrigger value="users">Users</TabsTrigger>
        </TabsList>
        <TabsContent className="flex flex-col gap-4" value="groups">
          <div className="flex flex-row items-center">
            <UsersIcon className="w-6 h-6"/>
            &nbsp;
            <h2>Groups</h2>
          </div>
          <DataTable<Group, unknown>
            recordKind="Group"
            columns={groupColumns}
            defaultColumnVisibility={groupColumnVisibility}
            page={groupPage}
            onSelect={setSelectedGroup}
          />
          <GroupDetails record={selectedGroup} />
          <p>See group members in the 'Users' tab</p>
        </TabsContent>
        <TabsContent className="flex flex-col gap-4" value="users">
          <div className="flex flex-row items-center">
            <UserIcon className="w-6 h-6"/>
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
            recordKind="User"
            columns={userColumns}
            defaultColumnVisibility={userColumnVisibility}
            page={showUsersOrMembers == "users" ? userPage : selectedGroup?.members}
            onSelect={setSelectedUser}
          />
          <UserDetails user={selectedUser} group={selectedGroup} showUsersOrMembers={showUsersOrMembers} />
        </TabsContent>
      </Tabs>
    </main>
  );
}
