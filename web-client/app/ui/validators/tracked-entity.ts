import { z } from "zod/v4"
import { UserSchema } from "./user"
import { Log } from "./log"
import { IBaseEntity } from "./base-entity"

export const EntityKind = z.enum([
  "CLA",
  "COU",
  "DEC",
  "GRP",
  "JOU",
  "LNK",
  "PER",
  "PUB",
  "PBR",
  "QUO",
  "TOP",
  "USR",
])

export const Status = z.enum([
  "DEL",
  "DRA",
  "PUB",
  "SUS",
])

export const ITrackedEntity = IBaseEntity.extend({
  entityKind: EntityKind,
  status: Status,
  created: z.iso.date(),
  // createdByUser: User,
  updated: z.iso.date().optional(),
  // updatedByUser: User.optional(),
  log: z.array(Log),
})