import { z } from "zod/v4"
import { ITrackedEntity } from "./tracked-entity"
import { IPage } from "./page"
import { EntityLink } from "./entity-link"

const EntityLinkPage = IPage.extend({
  content: z.array(EntityLink)
})

export const ILinkableEntity = ITrackedEntity.extend({
  fromEntityLinks: EntityLinkPage,
  toEntityLinks: EntityLinkPage,
})