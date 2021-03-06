begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|segment
operator|.
name|file
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|segment
operator|.
name|Revisions
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
name|segment
operator|.
name|SegmentCache
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
name|segment
operator|.
name|SegmentTracker
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|GCGeneration
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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|TarFiles
import|;
end_import

begin_interface
interface|interface
name|CleanupStrategy
block|{
interface|interface
name|Context
block|{
name|GCListener
name|getGCListener
parameter_list|()
function_decl|;
name|SegmentCache
name|getSegmentCache
parameter_list|()
function_decl|;
name|SegmentTracker
name|getSegmentTracker
parameter_list|()
function_decl|;
name|FileStoreStats
name|getFileStoreStats
parameter_list|()
function_decl|;
name|GCNodeWriteMonitor
name|getCompactionMonitor
parameter_list|()
function_decl|;
name|GCJournal
name|getGCJournal
parameter_list|()
function_decl|;
name|Predicate
argument_list|<
name|GCGeneration
argument_list|>
name|getReclaimer
parameter_list|()
function_decl|;
name|TarFiles
name|getTarFiles
parameter_list|()
function_decl|;
name|Revisions
name|getRevisions
parameter_list|()
function_decl|;
name|String
name|getCompactedRootId
parameter_list|()
function_decl|;
name|String
name|getSegmentEvictionReason
parameter_list|()
function_decl|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

