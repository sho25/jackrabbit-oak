begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|explorer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_interface
interface|interface
name|ExplorerBackend
block|{
name|void
name|open
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|void
name|close
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|readRevisions
parameter_list|()
function_decl|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarReaderIndex
parameter_list|()
function_decl|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|UUID
argument_list|>
argument_list|>
name|getTarGraph
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getTarFiles
parameter_list|()
function_decl|;
name|void
name|getGcRoots
parameter_list|(
name|UUID
name|uuidIn
parameter_list|,
name|Map
argument_list|<
name|UUID
argument_list|,
name|Set
argument_list|<
name|Entry
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|links
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|Set
argument_list|<
name|UUID
argument_list|>
name|getReferencedSegmentIds
parameter_list|()
function_decl|;
name|NodeState
name|getHead
parameter_list|()
function_decl|;
name|NodeState
name|readNodeState
parameter_list|(
name|String
name|recordId
parameter_list|)
function_decl|;
name|void
name|setRevision
parameter_list|(
name|String
name|revision
parameter_list|)
function_decl|;
name|boolean
name|isPersisted
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|boolean
name|isPersisted
parameter_list|(
name|PropertyState
name|state
parameter_list|)
function_decl|;
name|String
name|getRecordId
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|UUID
name|getSegmentId
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|String
name|getRecordId
parameter_list|(
name|PropertyState
name|state
parameter_list|)
function_decl|;
name|UUID
name|getSegmentId
parameter_list|(
name|PropertyState
name|state
parameter_list|)
function_decl|;
name|String
name|getTemplateRecordId
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|UUID
name|getTemplateSegmentId
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|String
name|getFile
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|String
name|getFile
parameter_list|(
name|PropertyState
name|state
parameter_list|)
function_decl|;
name|String
name|getTemplateFile
parameter_list|(
name|NodeState
name|state
parameter_list|)
function_decl|;
name|Map
argument_list|<
name|UUID
argument_list|,
name|String
argument_list|>
name|getBulkSegmentIds
parameter_list|(
name|Blob
name|blob
parameter_list|)
function_decl|;
name|String
name|getPersistedCompactionMapStats
parameter_list|()
function_decl|;
name|boolean
name|isExternal
parameter_list|(
name|Blob
name|blob
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

