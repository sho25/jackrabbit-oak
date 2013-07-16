begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|api
operator|.
name|PropertyValue
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
name|Result
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
name|Tree
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
name|namepath
operator|.
name|NamePathMapper
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
name|query
operator|.
name|ast
operator|.
name|ColumnImpl
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
name|query
operator|.
name|ast
operator|.
name|SelectorImpl
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
comment|/**  * A "select" or "union" query.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AbstractQuery
block|{
name|void
name|setRootTree
parameter_list|(
name|Tree
name|rootTree
parameter_list|)
function_decl|;
name|void
name|setRootState
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
function_decl|;
name|void
name|setNamePathMapper
parameter_list|(
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
name|void
name|setLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
name|void
name|setOffset
parameter_list|(
name|long
name|offset
parameter_list|)
function_decl|;
name|void
name|bindValue
parameter_list|(
name|String
name|key
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
function_decl|;
name|void
name|setQueryEngine
parameter_list|(
name|QueryEngineImpl
name|queryEngineImpl
parameter_list|)
function_decl|;
name|void
name|prepare
parameter_list|()
function_decl|;
name|Result
name|executeQuery
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|()
function_decl|;
name|ColumnImpl
index|[]
name|getColumns
parameter_list|()
function_decl|;
name|List
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
function_decl|;
name|Iterator
argument_list|<
name|ResultRowImpl
argument_list|>
name|getRows
parameter_list|()
function_decl|;
name|long
name|getSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

