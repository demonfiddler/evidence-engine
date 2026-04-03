/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

'use client'

import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "@/components/ui/combobox"
import { InputGroupAddon } from "@/components/ui/input-group";
import { countries, countryByAlpha2 } from "@/data/countries"
import { ControllerRenderProps, FieldValues } from "react-hook-form";
import Help from "../misc/help";

type CountryComboboxProps<TFieldValues extends FieldValues> = {
  id?: string
  className?: string
  disabled?: boolean
  help?: string
  field: ControllerRenderProps<TFieldValues>
}

export default function CountryCombobox<TFieldValues extends FieldValues>(
    { id = "country", className, field, disabled = false, help } : CountryComboboxProps<TFieldValues>) {

  const selectedCountry = field.value
  ? countryByAlpha2[field.value] ?? null
  : null

  return (
    <Combobox
      disabled={disabled}
      items={countries}
      itemToStringValue={c => c.alpha_2}
      itemToStringLabel={c => c.common_name}
      value={selectedCountry}
      onValueChange={c => field.onChange(c?.alpha_2 ?? null)}
    >
      <ComboboxInput className={className} id={id} placeholder="Select a country" readOnly={disabled} showClear>
        <InputGroupAddon align="inline-end">
          <Help text={help} />
        </InputGroupAddon>
      </ComboboxInput>
      <ComboboxContent>
        <ComboboxEmpty>-No countries found-</ComboboxEmpty>
        <ComboboxList>
          {c => (
            <ComboboxItem key={c.alpha_2} value={c}>
              {c.common_name}
            </ComboboxItem>
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  )
}
