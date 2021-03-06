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
name|bundlor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|commons
operator|.
name|PathUtils
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
name|commons
operator|.
name|json
operator|.
name|JsopWriter
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
name|AbstractDocumentNodeState
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
name|DocumentNodeState
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
name|DocumentNodeStore
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
name|Path
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
name|NodeStateUtils
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_class
specifier|public
class|class
name|BundledDocumentDiffer
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|public
name|BundledDocumentDiffer
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
comment|/**      * Performs diff for bundled nodes. The passed state can be DocumentNodeState or      * one from secondary nodestore i.e. {@code DelegatingDocumentNodeState}. So the      * passed states cannot be cast down to DocumentNodeState      *      * @param from from state      * @param to to state      * @param w jsop diff      * @return true if the diff needs to be continued. In case diff is complete it would return false      */
specifier|public
name|boolean
name|diff
parameter_list|(
name|AbstractDocumentNodeState
name|from
parameter_list|,
name|AbstractDocumentNodeState
name|to
parameter_list|,
name|JsopWriter
name|w
parameter_list|)
block|{
name|boolean
name|fromBundled
init|=
name|BundlorUtils
operator|.
name|isBundledNode
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|boolean
name|toBundled
init|=
name|BundlorUtils
operator|.
name|isBundledNode
argument_list|(
name|to
argument_list|)
decl_stmt|;
comment|//Neither of the nodes bundled
if|if
condition|(
operator|!
name|fromBundled
operator|&&
operator|!
name|toBundled
condition|)
block|{
return|return
literal|true
return|;
block|}
name|DocumentNodeState
name|fromDocState
init|=
name|getDocumentNodeState
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|DocumentNodeState
name|toDocState
init|=
name|getDocumentNodeState
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|diffChildren
argument_list|(
name|fromDocState
operator|.
name|getBundledChildNodeNames
argument_list|()
argument_list|,
name|toDocState
operator|.
name|getBundledChildNodeNames
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
comment|//If all child nodes are bundled then diff is complete
if|if
condition|(
name|fromDocState
operator|.
name|hasOnlyBundledChildren
argument_list|()
operator|&&
name|toDocState
operator|.
name|hasOnlyBundledChildren
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
name|void
name|diffChildren
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|fromChildren
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|toChildren
parameter_list|,
name|JsopWriter
name|w
parameter_list|)
block|{
for|for
control|(
name|String
name|n
range|:
name|fromChildren
control|)
block|{
if|if
condition|(
operator|!
name|toChildren
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|w
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//As lastRev for bundled node is same as parent node and they differ it means
comment|//children "may" also diff
name|w
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|n
argument_list|)
operator|.
name|object
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|n
range|:
name|toChildren
control|)
block|{
if|if
condition|(
operator|!
name|fromChildren
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|w
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|n
argument_list|)
operator|.
name|object
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|DocumentNodeState
name|getDocumentNodeState
parameter_list|(
name|AbstractDocumentNodeState
name|state
parameter_list|)
block|{
name|DocumentNodeState
name|result
decl_stmt|;
comment|//Shortcut - If already a DocumentNodeState use as it. In case of SecondaryNodeStore
comment|//it can be DelegatingDocumentNodeState. In that case we need to read DocumentNodeState from
comment|//DocumentNodeStore and then get to DocumentNodeState for given path
if|if
condition|(
name|state
operator|instanceof
name|DocumentNodeState
condition|)
block|{
name|result
operator|=
operator|(
name|DocumentNodeState
operator|)
name|state
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BundlorUtils
operator|.
name|isBundledChild
argument_list|(
name|state
argument_list|)
condition|)
block|{
comment|//In case of bundle child determine the bundling root
comment|//and from there traverse down to the actual child node
name|checkState
argument_list|(
name|BundlorUtils
operator|.
name|isBundledChild
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|bundlingPath
init|=
name|state
operator|.
name|getString
argument_list|(
name|DocumentBundlor
operator|.
name|META_PROP_BUNDLING_PATH
argument_list|)
decl_stmt|;
name|Path
name|bundlingRootPath
init|=
name|state
operator|.
name|getPath
argument_list|()
operator|.
name|getAncestor
argument_list|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|bundlingPath
argument_list|)
argument_list|)
decl_stmt|;
name|DocumentNodeState
name|bundlingRoot
init|=
name|nodeStore
operator|.
name|getNode
argument_list|(
name|bundlingRootPath
argument_list|,
name|state
operator|.
name|getLastRevision
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
operator|(
name|DocumentNodeState
operator|)
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|bundlingRoot
argument_list|,
name|bundlingPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|nodeStore
operator|.
name|getNode
argument_list|(
name|state
operator|.
name|getPath
argument_list|()
argument_list|,
name|state
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|checkNotNull
argument_list|(
name|result
argument_list|,
literal|"Node at [%s] not found for fromRev [%s]"
argument_list|,
name|state
operator|.
name|getPath
argument_list|()
argument_list|,
name|state
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

