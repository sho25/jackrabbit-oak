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
name|OrderingImpl
import|;
end_import

begin_comment
comment|/**  * A "select" or "union" query.  *<p>  * Lifecycle: use the constructor to create a new object. Call init() to  * initialize the bind variable map. If the query is re-executed, a new instance  * is created.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Query
block|{
name|void
name|setExecutionContext
parameter_list|(
name|ExecutionContext
name|context
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
name|setTraversalEnabled
parameter_list|(
name|boolean
name|traversalEnabled
parameter_list|)
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
name|int
name|getColumnIndex
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
name|String
index|[]
name|getSelectorNames
parameter_list|()
function_decl|;
name|int
name|getSelectorIndex
parameter_list|(
name|String
name|selectorName
parameter_list|)
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
name|void
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
function_decl|;
name|void
name|setMeasure
parameter_list|(
name|boolean
name|measure
parameter_list|)
function_decl|;
name|void
name|setOrderings
parameter_list|(
name|OrderingImpl
index|[]
name|orderings
parameter_list|)
function_decl|;
comment|/**      * Initialize the query. This will 'wire' selectors into constraints bind      * variables into expressions. It will also simplify expressions if      * possible, but will not prepare the query.      */
name|void
name|init
parameter_list|()
function_decl|;
comment|/**      * Prepare the query. The cost is estimated and the execution plan is      * decided here.      */
name|void
name|prepare
parameter_list|()
function_decl|;
comment|/**      * Get the query plan. The query must already be prepared.      *       * @return the query plan      */
name|String
name|getPlan
parameter_list|()
function_decl|;
comment|/**      * Get the estimated cost.      *       * @return the estimated cost      */
name|double
name|getEstimatedCost
parameter_list|()
function_decl|;
name|Tree
name|getTree
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

