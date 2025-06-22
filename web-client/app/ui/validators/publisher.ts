import { z } from "zod/v4"
// import { ITrackedEntity } from "./tracked-entity"

export const PublisherSchema = /*ITrackedEntity.partial().extend(*/z.object({
  name: z.string().min(2).max(200),
  location: z.string().max(50).optional(),
  country: z.string().length(0).or(z.string().uppercase().length(2)).optional(),
  url: z.url().min(10).max(200).or(z.string().length(0)).optional(),
  journalCount: z.string().regex(/^\d*$/).or(z.uint32()).optional()
})

export type PublisherFormFields = z.infer<typeof PublisherSchema>