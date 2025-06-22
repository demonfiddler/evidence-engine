// import type { AuthoritiesFormFields } from "@/app/ui/validators/authority"

export type AuthorityUI = {
  key: "adm" | "cre" | "del" | "lnk" | "rea" | "upd" | "upl"
  label: string
  description: string
}

// AuthoritiesFormFields

export const authorities : AuthorityUI[] = [
  {key: "adm", label: "Administer", description: "Administer system"},
  {key: "cre", label: "Create", description: "Create records"},
  {key: "del", label: "Delete", description: "Delete records"},
  {key: "lnk", label: "Link", description: "Link records"},
  {key: "rea", label: "Read", description: "Read records"},
  {key: "upd", label: "Update", description: "Update records"},
  {key: "upl", label: "Upload", description: "Upload files"},
]