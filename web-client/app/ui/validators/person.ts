import { z } from "zod/v4"
// import { ILinkableEntity } from "./linkable-entity"

export const PersonSchema = /*ILinkableEntity.partial().extend*/z.object({
  title: z.string().max(10).optional(),
  firstName: z.string().max(80).optional(),
  nickname: z.string().max(40).optional(),
  prefix: z.string().max(20).optional(),
  lastName: z.string().min(1).max(40),
  suffix: z.string().max(16).optional(),
  alias: z.string().max(40).optional(),
  notes: z.string().optional(),
  qualifications: z.string().optional(),
  country: z.string().uppercase().length(2).optional(),
  rating: z.string().regex(/^[1-5]?$/).or(z.uint32().min(1).max(5)).optional(),
  checked: z.boolean(),
  published: z.boolean()
})

export type PersonFormFields = z.infer<typeof PersonSchema>