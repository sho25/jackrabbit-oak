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
name|plugins
operator|.
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
import|;
end_import

begin_comment
comment|/**  * NodeTypeConstants... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeTypeConstants
extends|extends
name|JcrConstants
block|{
name|String
name|JCR_NODE_TYPES
init|=
literal|"jcr:nodeTypes"
decl_stmt|;
name|String
name|NODE_TYPES_PATH
init|=
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|'/'
operator|+
name|JCR_NODE_TYPES
decl_stmt|;
name|String
name|JCR_IS_ABSTRACT
init|=
literal|"jcr:isAbstract"
decl_stmt|;
name|String
name|JCR_IS_QUERYABLE
init|=
literal|"jcr:isQueryable"
decl_stmt|;
name|String
name|JCR_IS_FULLTEXT_SEARCHABLE
init|=
literal|"jcr:isFullTextSearchable"
decl_stmt|;
name|String
name|JCR_IS_QUERY_ORDERABLE
init|=
literal|"jcr:isQueryOrderable"
decl_stmt|;
name|String
name|JCR_AVAILABLE_QUERY_OPERATORS
init|=
literal|"jcr:availableQueryOperators"
decl_stmt|;
comment|/**      * Name of the mixin type to mark merge conflicts      * TODO: review where this constant should be located.      */
name|String
name|MIX_REP_MERGE_CONFLICT
init|=
literal|"rep:MergeConflict"
decl_stmt|;
block|}
end_interface

end_unit

