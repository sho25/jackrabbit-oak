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
name|plugins
operator|.
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|Collection
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
name|plugins
operator|.
name|document
operator|.
name|MissingLastRevSeeker
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
name|plugins
operator|.
name|document
operator|.
name|NodeDocument
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBDocumentStore
operator|.
name|QueryCondition
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|CloseableIterable
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
name|stats
operator|.
name|Clock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * RDB specific version of MissingLastRevSeeker.  */
end_comment

begin_class
specifier|public
class|class
name|RDBMissingLastRevSeeker
extends|extends
name|MissingLastRevSeeker
block|{
specifier|private
specifier|final
name|RDBDocumentStore
name|store
decl_stmt|;
specifier|public
name|RDBMissingLastRevSeeker
parameter_list|(
name|RDBDocumentStore
name|store
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|,
name|clock
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|getCandidates
parameter_list|(
specifier|final
name|long
name|startTime
parameter_list|)
block|{
name|List
argument_list|<
name|QueryCondition
argument_list|>
name|conditions
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|QueryCondition
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|,
literal|">="
argument_list|,
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|startTime
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|queryAsIterable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|RDBDocumentStore
operator|.
name|EMPTY_KEY_PATTERN
argument_list|,
name|conditions
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

