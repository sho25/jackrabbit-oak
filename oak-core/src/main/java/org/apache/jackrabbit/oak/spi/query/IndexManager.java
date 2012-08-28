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
name|spi
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|commit
operator|.
name|CommitEditor
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

begin_comment
comment|/**  *<p>  * Index Manager keeps track of all the available indexes.  *</p>  *   *<p>  * As a configuration reference it will use the index definitions nodes at  * {@link IndexUtils#DEFAULT_INDEX_HOME}.  *</p>  *   *<p>  * TODO Document simple node properties to create an index type  *</p>  *</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexManager
extends|extends
name|CommitEditor
extends|,
name|Closeable
block|{
name|void
name|registerIndexFactory
parameter_list|(
name|IndexFactory
modifier|...
name|factory
parameter_list|)
function_decl|;
name|void
name|unregisterIndexFactory
parameter_list|(
name|IndexFactory
name|factory
parameter_list|)
function_decl|;
comment|/**      * @return the index with the given name      */
annotation|@
name|CheckForNull
name|Index
name|getIndex
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
comment|/**      * @return the index with the given definition      */
annotation|@
name|CheckForNull
specifier|public
name|Index
name|getIndex
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|)
function_decl|;
comment|/**      * @return the existing index definitions      */
annotation|@
name|Nonnull
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|getIndexDefinitions
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

