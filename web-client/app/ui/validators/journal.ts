import { z } from "zod/v4"
// import { ITrackedEntity } from "./tracked-entity"

export const JournalSchema = /*ITrackedEntity.partial().extend*/z.object({
  title: z.string().min(10).max(100),
  abbreviation: z.string().max(50).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  issn: z.string().regex(/(?:^[0-9]{4}-[0-9]{3}[0-9X]$)?/).optional(),
  publisherId: z.string().regex(/^\d*$/).optional(),
  notes: z.string().max(200).optional()
})

export type JournalFormFields = z.infer<typeof JournalSchema>