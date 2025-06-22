import { z } from "zod/v4"
// import { ILinkableEntity } from "./linkable-entity"

export const ClaimSchema = /*ILinkableEntity.partial().extend*/z.object({
  text: z.string().min(15).max(500),
  date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).or(z.date().max(Date.now(), {error: "Claim date cannot be in the future"})),
  notes: z.string().optional()
})

export type ClaimFormFields = z.infer<typeof ClaimSchema>