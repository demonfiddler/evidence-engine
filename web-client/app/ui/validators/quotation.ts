import { z } from "zod/v4"
// import { ILinkableEntity } from "./linkable-entity"

export const QuotationSchema = /*ILinkableEntity.partial().extend*/z.object({
  quotee: z.string().min(5).max(50),
  text: z.string().max(1000),
  date: z.iso.date().or(z.date()).optional(),
  source: z.string().max(200).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  notes: z.string().nullable().optional()
})

export type QuotationFormFields = z.infer<typeof QuotationSchema>