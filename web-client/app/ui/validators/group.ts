import { z } from "zod/v4"
import { AuthoritiesSchema } from "./authority"

export const GroupSchema = /*AuthoritiesSchema.extend*/z.object({
  groupname: z.string().regex(/^[a-zA-Z0-9]*$/).min(1).max(50),
  // description: z.string().max(100).optional(),
  ...AuthoritiesSchema.shape
})

export type GroupFormFields = z.infer<typeof GroupSchema>