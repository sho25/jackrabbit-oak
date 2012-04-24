begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commit
operator|.
name|Validator
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
name|commit
operator|.
name|ValidatorProvider
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
name|kernel
operator|.
name|ChildNodeEntry
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
name|kernel
operator|.
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|TypeValidatorProvider
implements|implements
name|ValidatorProvider
block|{
annotation|@
name|Override
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Default JCR types are always available
name|types
operator|.
name|add
argument_list|(
literal|"nt:base"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:hierarchyNode"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:file"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:linkedFile"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:resource"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:title"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:created"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:lastModified"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:language"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:mimeType"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:address"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:etag"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:referenceable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:lockable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:shareable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:simpleVersionable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:versionHistory"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:versionLabels"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:version"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:frozenNode"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:versionedChild"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:activity"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:configuration"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:nodeType"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:propertyDefinition"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:childNodeDefinition"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"nt:query"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"mix:lifecycle"
argument_list|)
expr_stmt|;
comment|// Jackrabbit 2.x types are always available
name|types
operator|.
name|add
argument_list|(
literal|"rep:root"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:system"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:nodeTypes"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:versionStorage"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Activities"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Configurations"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:VersionReference"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:AccessControllable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:RepoAccessControllable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Policy"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:ACL"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:ACE"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:GrantACE"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:DenyACE"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:AccessControl"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:PrincipalAccessControl"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Authorizable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Impersonatable"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:User"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Group"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:AuthorizableFolder"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:Members"
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
literal|"rep:RetentionManageable"
argument_list|)
expr_stmt|;
comment|// Find any extra types from /jcr:system/jcr:nodeTypes
name|NodeState
name|system
init|=
name|after
operator|.
name|getChildNode
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
if|if
condition|(
name|system
operator|!=
literal|null
condition|)
block|{
name|NodeState
name|registry
init|=
name|system
operator|.
name|getChildNode
argument_list|(
literal|"jcr:nodeTypes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|registry
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ChildNodeEntry
name|child
range|:
name|registry
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|types
operator|.
name|add
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|TypeValidator
argument_list|(
name|types
argument_list|)
return|;
block|}
block|}
end_class

end_unit

