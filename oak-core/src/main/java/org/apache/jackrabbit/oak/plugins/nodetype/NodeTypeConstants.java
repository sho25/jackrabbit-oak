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
name|nodetype
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
comment|/**      * Constants for built-in repository defined node type names      */
name|String
name|NT_REP_ROOT
init|=
literal|"rep:root"
decl_stmt|;
name|String
name|NT_REP_SYSTEM
init|=
literal|"rep:system"
decl_stmt|;
comment|/**      * Additinal name constants not present in JcrConstants      */
name|String
name|JCR_CREATEDBY
init|=
literal|"jcr:createdBy"
decl_stmt|;
name|String
name|JCR_LASTMODIFIEDBY
init|=
literal|"jcr:lastModifiedBy"
decl_stmt|;
name|String
name|MIX_CREATED
init|=
literal|"mix:created"
decl_stmt|;
name|String
name|MIX_LASTMODIFIED
init|=
literal|"mix:lastModified"
decl_stmt|;
comment|/**      * Merge conflict handling      */
name|String
name|MIX_REP_MERGE_CONFLICT
init|=
literal|"rep:MergeConflict"
decl_stmt|;
name|String
name|REP_OURS
init|=
literal|"rep:ours"
decl_stmt|;
name|String
name|ADD_EXISTING
init|=
literal|"addExisting"
decl_stmt|;
name|String
name|CHANGE_DELETED
init|=
literal|"changeDeleted"
decl_stmt|;
name|String
name|CHANGE_CHANGED
init|=
literal|"changeChanged"
decl_stmt|;
name|String
name|DELETE_CHANGED
init|=
literal|"deleteChanged"
decl_stmt|;
name|String
name|DELETE_DELETED
init|=
literal|"deleteDeleted"
decl_stmt|;
block|}
end_interface

end_unit

