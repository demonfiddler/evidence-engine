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

from logging import getLogger
from abc import ABC, abstractmethod
from langgraph.graph import END, START, StateGraph
from pydantic import BaseModel
from typing import Generic, TypeVar, final, get_args

from ee_ai_service.runtime.runtime_state import RuntimeState

logger = getLogger(__name__)

ResultT = TypeVar("ResultT")

# class WorkflowState(BaseModel, Generic[ResultT]):
class WorkflowState(BaseModel):
    # result: ResultT | None = None
    pass

# StateT = TypeVar("StateT", bound = WorkflowState[ResultT])
StateT = TypeVar("StateT", bound = WorkflowState)

class Workflow(Generic[StateT, ResultT], ABC):
    "Abstract base class for workflow implementations."

    def __init__(self, runtime: RuntimeState):
        self.runtime = runtime

        # Create the graph with the actual runtime StateT type from the subclass
        state_type = get_args(self.__orig_bases__[0])[0]
        self.graph = StateGraph(state_type)

        self.register_nodes()
        self.wire_edges()
        self.workflow = self.graph.compile()

    @abstractmethod
    def register_nodes(self):
        pass

    @abstractmethod
    def wire_edges(self):
        pass

    def validate(self, state: StateT, runtime: RuntimeState):
        """
        Performs minimal, structural validation that applies to ALL workflows.
        This ensures the workflow cannot run with obviously invalid runtime
        dependencies or malformed state containers.

        Subclasses may extend this method, but should not weaken these checks.

        Args:
            state: The initial workflow state.
            runtime: The global runtime state.
        Raises:
            ValueError if the workflow or runtime state is invalid in any way.
        """

        if runtime is None:
            raise ValueError("runtime must not be None")

        if getattr(runtime, "inference_client", None) is None:
            raise ValueError("runtime.inference_client must not be None")

        if getattr(runtime, "embedding_client", None) is None:
            raise ValueError("runtime.embedding_client must not be None")

        if runtime.config is None:
            raise ValueError("runtime.config must not be None")

        if state is None:
            raise ValueError("state must not be None")

        # --- State must be a Pydantic model or dict-like ---
        if not hasattr(state, "__dict__") and not isinstance(state, dict):
            raise ValueError("state must be a Pydantic model or dict-like object")

        # --- Graph must be structurally valid ---
        if not hasattr(self, "graph"):
            raise ValueError("workflow must define a graph")

        nodes = getattr(self.graph, "nodes", None)
        if not nodes or not isinstance(nodes, dict):
            raise ValueError("workflow.graph.nodes must be a dict of node definitions")

        # All edges must reference valid nodes
        edges = getattr(self.graph, "edges", None)
        if edges:
            for src, dst in edges:
                if src is not START and src not in nodes:
                    raise ValueError(f"Graph edge references unknown node: {src}")
                if dst is not END and dst not in nodes:
                    raise ValueError(f"Graph edge references unknown node: {dst}")

        # If we reach here, the workflow is structurally valid
        return True

    @abstractmethod
    def get_result(self, state: dict[str, any]) -> ResultT:
        """
        Extracts the result from the final workflow state.

        Args:
            state: The final workflow state.
        Returns:
            The result extracted from the final workflow state.
        """
        pass

    @final
    async def run(self, state: StateT) -> ResultT:
        """
        Runs the workflow asynchronously.

        Args:
            state: The initial workflow state.
        Returns:
            The final workflow result state.
        """

        final_state = await self.workflow.ainvoke(state, config = {"metadata": {"runtime": self.runtime}})
        result = self.get_result(final_state)

        logger.info(f"Workflow {self.__class__.__name__} returned result: {result}")

        return result
