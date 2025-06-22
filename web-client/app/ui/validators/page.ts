import { z } from "zod/v4"
import { IBaseEntity } from "./base-entity"

export const IPage = z.object({
  hasContent: z.boolean(),
  isEmpty: z.boolean(),
  number: z.uint32().min(0),
  size: z.uint32().min(0),
  numberOfElements: z.uint32().min(0),
  totalPages: z.uint32().min(0),
  totalElements: z.string().regex(/^\d+$/),
  isFirst: z.boolean(),
  isLast: z.boolean(),
  hasNext: z.boolean(),
  hasPrevious: z.boolean(),
  content: z.array(IBaseEntity),
})