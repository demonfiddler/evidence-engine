import { z } from "zod/v4"

export const TopicSchema = z.object({
  path: z.string(),
  label: z.string().min(2).max(50),
  description: z.string().max(500).optional(),
  parentId: z.string().regex(/^\d*$/).optional(),
})

export type TopicFormFields = z.infer<typeof TopicSchema>