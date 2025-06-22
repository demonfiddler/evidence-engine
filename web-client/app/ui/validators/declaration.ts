import { z } from "zod/v4"
// import { ILinkableEntity } from "./linkable-entity"

export const DeclarationKindSchema = z.enum([
  "DECL",
  "OPLE",
  "PETN",
])

export const DeclarationSchema = /*ILinkableEntity.partial().extend*/z.object({
  kind: DeclarationKindSchema,
  date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).or(z.date().max(Date.now(), {error: "Date cannot be in the future"})),
  title: z.string().min(10).max(100),
  country: z.string().uppercase().length(2).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).nullable().optional(),
  cached: z.boolean(),
  signatories: z.string().optional(),
  signatoryCount: z.string().regex(/^\d*$/).or(z.uint32()).optional(),
  notes: z.string().optional()
})

export type DeclarationFormFields = z.infer<typeof DeclarationSchema>
export type DeclarationKind = z.infer<typeof DeclarationKindSchema>