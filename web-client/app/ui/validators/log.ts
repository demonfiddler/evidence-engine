import { z } from "zod/v4"
import { UserSchema } from "./user"
import { EntityKind } from "./tracked-entity"
import { ID } from "./id"

const TransactionKind = z.enum([
  "CRE",
  "DEL",
  "LNK",
  "UNL",
  "UPD",
])

export const Log = z.object({
  timestamp: z.iso.datetime().optional(),
  // user: User.optional(),
  transactionKind: TransactionKind.optional(),
  entityKind: EntityKind.optional(),
  entityId: ID.optional(),
  linkedEntityKind: EntityKind.optional(),
  linkedEntityId: ID.optional(),
})