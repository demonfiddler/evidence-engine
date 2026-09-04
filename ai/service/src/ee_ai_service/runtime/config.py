# ----------------------------------------------------------------------------------------------------------------------
#  Evidence Engine: A system for managing evidence on arbitrary scientific topics.
#  Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
#  Copyright © 2024-26 Adrian Price. All rights reserved.
#
#  This file is part of Evidence Engine.
#
#  Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
#  GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
#  or (at your option) any later version.
#
#  Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
#  If not, see <https://www.gnu.org/licenses/>. 
# ----------------------------------------------------------------------------------------------------------------------

"""Application configuration."""

from __future__ import annotations
from configparser import ConfigParser
from importlib.resources import files
import logging
import os

class Config:
    @staticmethod
    def load(filename: str) -> Config:
        """Load configuration from a file or package resource."""

        parser = ConfigParser()
        if os.path.exists(filename):
            # 1. External override (disk)
            parser.read(filename)
        else:
            # 2. Wheel‑embedded fallback
            config_path = files("ee_ai_service.runtime").joinpath(filename)
            if config_path.is_file():
                parser.read(config_path)
            else:
                raise FileNotFoundError(
                    f"Config file '{filename}' not found on disk or in package."
                )

        config = Config()

        ai = parser['ai']
        config.ollama_url = ai['ollama_url']
        config.api_key = ai.get('api_key')
        config.inference_model = ai['inference_model']
        config.embedding_model = ai['embedding_model']
        config.existing_threshold = ai.getfloat('existing_threshold')
        config.discovered_threshold = ai.getfloat('discovered_threshold')

        ee = parser['ee']
        config.ee_graphql_url = ee['graphql_url']
        config.rest_base_url = ee['rest_base_url']
        config.username = ee['username']
        config.password = ee['password']

        misc = parser['misc']
        config.log_level = getattr(logging, misc['log_level'].upper(), logging.INFO)

        return config
