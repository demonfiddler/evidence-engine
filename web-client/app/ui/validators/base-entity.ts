import { z } from "zod/v4"
import { ID } from "./id";

export const IBaseEntity = z.object({
  id: ID,
})