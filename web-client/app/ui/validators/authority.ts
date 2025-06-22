import { z } from "zod/v4"

export const Authority = z.enum([
  "ADM",
  "CRE",
  "DEL",
  "LNK",
  "REA",
  "UPD",
  "UPL"
])

export const AuthoritiesSchema = z.object({
  adm: z.boolean(),
  cre: z.boolean(),
  del: z.boolean(),
  lnk: z.boolean(),
  rea: z.boolean(),
  upd: z.boolean(),
  upl: z.boolean(),
})

export type AuthoritiesFormFields = z.infer<typeof AuthoritiesSchema>