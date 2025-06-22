import { z } from "zod/v4"
import { AuthoritiesSchema } from "./authority"

export const UserSchema = /*AuthoritiesSchema.extend*/z.object({
  username: z.string().regex(/^[a-z0-9]*$/).min(1).max(50),
  password: z.string().regex(/^\{bcrypt\}\$[a-zA-Z0-9/$.]{59}$/).optional(),
  firstName: z.string().min(1).max(50),
  lastName: z.string().min(1).max(50),
  email: z.email().max(100),
  country: z.string().uppercase().length(2).optional(),
  notes: z.string().optional(),
  ...AuthoritiesSchema.shape
})

export type UserFormFields = z.infer<typeof UserSchema>