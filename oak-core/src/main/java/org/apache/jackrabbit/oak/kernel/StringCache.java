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
name|kernel
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|version
operator|.
name|VersionConstants
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
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|StringCache
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|CONSTANTS
init|=
name|createStringMap
argument_list|(
name|JcrConstants
operator|.
name|JCR_AUTOCREATED
argument_list|,
name|JcrConstants
operator|.
name|JCR_BASEVERSION
argument_list|,
name|JcrConstants
operator|.
name|JCR_CHILD
argument_list|,
name|JcrConstants
operator|.
name|JCR_CHILDNODEDEFINITION
argument_list|,
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|,
name|JcrConstants
operator|.
name|JCR_CREATED
argument_list|,
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|,
name|JcrConstants
operator|.
name|JCR_DEFAULTPRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_DEFAULTVALUES
argument_list|,
name|JcrConstants
operator|.
name|JCR_ENCODING
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENMIXINTYPES
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENNODE
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENPRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_FROZENUUID
argument_list|,
name|JcrConstants
operator|.
name|JCR_HASORDERABLECHILDNODES
argument_list|,
name|JcrConstants
operator|.
name|JCR_ISCHECKEDOUT
argument_list|,
name|JcrConstants
operator|.
name|JCR_ISMIXIN
argument_list|,
name|JcrConstants
operator|.
name|JCR_LANGUAGE
argument_list|,
name|JcrConstants
operator|.
name|JCR_LASTMODIFIED
argument_list|,
name|JcrConstants
operator|.
name|JCR_LOCKISDEEP
argument_list|,
name|JcrConstants
operator|.
name|JCR_LOCKOWNER
argument_list|,
name|JcrConstants
operator|.
name|JCR_MANDATORY
argument_list|,
name|JcrConstants
operator|.
name|JCR_MERGEFAILED
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|JcrConstants
operator|.
name|JCR_MULTIPLE
argument_list|,
name|JcrConstants
operator|.
name|JCR_NAME
argument_list|,
name|JcrConstants
operator|.
name|JCR_NODETYPENAME
argument_list|,
name|JcrConstants
operator|.
name|JCR_ONPARENTVERSION
argument_list|,
name|JcrConstants
operator|.
name|JCR_PREDECESSORS
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYITEMNAME
argument_list|,
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_PROPERTYDEFINITION
argument_list|,
name|JcrConstants
operator|.
name|JCR_PROTECTED
argument_list|,
name|JcrConstants
operator|.
name|JCR_REQUIREDPRIMARYTYPES
argument_list|,
name|JcrConstants
operator|.
name|JCR_REQUIREDTYPE
argument_list|,
name|JcrConstants
operator|.
name|JCR_ROOTVERSION
argument_list|,
name|JcrConstants
operator|.
name|JCR_SAMENAMESIBLINGS
argument_list|,
name|JcrConstants
operator|.
name|JCR_STATEMENT
argument_list|,
name|JcrConstants
operator|.
name|JCR_SUCCESSORS
argument_list|,
name|JcrConstants
operator|.
name|JCR_SUPERTYPES
argument_list|,
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|,
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|JcrConstants
operator|.
name|JCR_VALUECONSTRAINTS
argument_list|,
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
argument_list|,
name|JcrConstants
operator|.
name|JCR_VERSIONLABELS
argument_list|,
name|JcrConstants
operator|.
name|JCR_VERSIONSTORAGE
argument_list|,
name|JcrConstants
operator|.
name|JCR_VERSIONABLEUUID
argument_list|,
name|JcrConstants
operator|.
name|JCR_PATH
argument_list|,
name|JcrConstants
operator|.
name|JCR_SCORE
argument_list|,
name|JcrConstants
operator|.
name|MIX_LOCKABLE
argument_list|,
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|,
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|,
name|JcrConstants
operator|.
name|MIX_SHAREABLE
argument_list|,
name|JcrConstants
operator|.
name|NT_BASE
argument_list|,
name|JcrConstants
operator|.
name|NT_CHILDNODEDEFINITION
argument_list|,
name|JcrConstants
operator|.
name|NT_FILE
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|,
name|JcrConstants
operator|.
name|NT_FROZENNODE
argument_list|,
name|JcrConstants
operator|.
name|NT_HIERARCHYNODE
argument_list|,
name|JcrConstants
operator|.
name|NT_LINKEDFILE
argument_list|,
name|JcrConstants
operator|.
name|NT_NODETYPE
argument_list|,
name|JcrConstants
operator|.
name|NT_PROPERTYDEFINITION
argument_list|,
name|JcrConstants
operator|.
name|NT_QUERY
argument_list|,
name|JcrConstants
operator|.
name|NT_RESOURCE
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|,
name|JcrConstants
operator|.
name|NT_VERSION
argument_list|,
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
argument_list|,
name|JcrConstants
operator|.
name|NT_VERSIONLABELS
argument_list|,
name|JcrConstants
operator|.
name|NT_VERSIONEDCHILD
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_IS_ABSTRACT
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_IS_QUERYABLE
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_IS_FULLTEXT_SEARCHABLE
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_IS_QUERY_ORDERABLE
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_AVAILABLE_QUERY_OPERATORS
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_REP_ROOT
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_REP_SYSTEM
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_CREATEDBY
argument_list|,
name|NodeTypeConstants
operator|.
name|JCR_LASTMODIFIEDBY
argument_list|,
name|NodeTypeConstants
operator|.
name|MIX_CREATED
argument_list|,
name|NodeTypeConstants
operator|.
name|MIX_LASTMODIFIED
argument_list|,
name|NodeTypeConstants
operator|.
name|MIX_REP_MERGE_CONFLICT
argument_list|,
name|NodeTypeConstants
operator|.
name|REP_OURS
argument_list|,
name|NodeTypeConstants
operator|.
name|ADD_EXISTING
argument_list|,
name|NodeTypeConstants
operator|.
name|CHANGE_DELETED
argument_list|,
name|NodeTypeConstants
operator|.
name|CHANGE_CHANGED
argument_list|,
name|NodeTypeConstants
operator|.
name|DELETE_CHANGED
argument_list|,
name|NodeTypeConstants
operator|.
name|DELETE_DELETED
argument_list|,
name|VersionConstants
operator|.
name|JCR_ACTIVITY
argument_list|,
name|VersionConstants
operator|.
name|JCR_ACTIVITIES
argument_list|,
name|VersionConstants
operator|.
name|JCR_ACTIVITY_TITLE
argument_list|,
name|VersionConstants
operator|.
name|NT_ACTIVITY
argument_list|,
name|VersionConstants
operator|.
name|REP_ACTIVITIES
argument_list|,
name|VersionConstants
operator|.
name|JCR_CONFIGURATION
argument_list|,
name|VersionConstants
operator|.
name|JCR_CONFIGURATIONS
argument_list|,
name|VersionConstants
operator|.
name|JCR_ROOT
argument_list|,
name|VersionConstants
operator|.
name|NT_CONFIGURATION
argument_list|,
name|VersionConstants
operator|.
name|REP_CONFIGURATIONS
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createStringMap
parameter_list|(
name|String
modifier|...
name|strings
parameter_list|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|string
argument_list|,
name|string
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|// must be a power of 2
specifier|private
specifier|static
specifier|final
name|int
name|STRING_CACHE_SIZE
init|=
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|STRING_CACHE
init|=
operator|new
name|String
index|[
name|STRING_CACHE_SIZE
index|]
decl_stmt|;
specifier|public
specifier|static
name|String
name|get
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|String
name|constant
init|=
name|CONSTANTS
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|constant
operator|!=
literal|null
condition|)
block|{
return|return
name|constant
return|;
block|}
name|int
name|index
init|=
name|s
operator|.
name|hashCode
argument_list|()
operator|&
operator|(
name|STRING_CACHE_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
name|String
name|cached
init|=
name|STRING_CACHE
index|[
name|index
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|cached
argument_list|)
condition|)
block|{
name|cached
operator|=
operator|new
name|String
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|// avoid referring to
name|STRING_CACHE
index|[
name|index
index|]
operator|=
name|cached
expr_stmt|;
block|}
return|return
name|cached
return|;
block|}
block|}
end_class

end_unit

