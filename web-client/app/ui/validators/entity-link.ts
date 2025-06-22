import { z } from "zod/v4"
// import { ITrackedEntity } from "./tracked-entity"
import { ILinkableEntity } from "./linkable-entity"

export const EntityLink = /*ITrackedEntity.extend*/z.object({
  fromEntity: ILinkableEntity,
  fromEntityLocations: z.string().optional(),
  toEntity: ILinkableEntity,
  toEntityLocations: z.string().optional(),
})