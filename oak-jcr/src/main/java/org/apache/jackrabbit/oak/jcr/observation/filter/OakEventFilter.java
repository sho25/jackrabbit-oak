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
name|jcr
operator|.
name|observation
operator|.
name|filter
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
name|api
operator|.
name|observation
operator|.
name|JackrabbitEventFilter
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ConsumerType
import|;
end_import

begin_comment
comment|/**  * Extension of the JackrabbitEventFilter that exposes Oak specific  * features.  *<p>  * Usage:  *<code>  * OakEventFilter oakFilter = FilterFactory.wrap(jackrabbitFilter);  * // then call extensions on OakEventFilters  * observationManager.addEventListener(listener, oakFilter);    *</code>  */
end_comment

begin_class
annotation|@
name|ConsumerType
specifier|public
specifier|abstract
class|class
name|OakEventFilter
extends|extends
name|JackrabbitEventFilter
block|{
comment|/**      * This causes the node type filter to be applied on 'this' node instead of      * the 'parent' node, thus allows to create a filter which listens on      * adding/removing/etc on nodes of a particular node type (while the default      * was that the node type was applicable on the parent).      *<p>      * Note that this is an 'either/or' thing: either the node type is applied      * on the parent (default) or on 'self/this' (via this switch) but not both.      *<p>      * Also note that this is independent from the nodeTypeAggregate,       * and will only be applied to the (main) node types set on this filter,      * not on the nodeTypeAggregate's node types.      * @return this filter with the filter change applied      */
specifier|public
specifier|abstract
name|OakEventFilter
name|withApplyNodeTypeOnSelf
parameter_list|()
function_decl|;
comment|/**      * This causes the registration of independent, additional      * !deep NODE_REMOVED filter-conditions      * of all parents of the include paths (both normal and glob).      * (These additional filter-conditions are added in 'OR' mode      * to the otherwise resulting filter-conditions, thus you can      * still have an independent<code>deep/!deep</code> flag too)      *<ul>      *<li>include path /a/b/c/d results in additional !deep NODE_REMOVED      * filters on /a/b/c, on /a/b and on /a</li>      *<li>include path /a/b/** results in additional !deep NODE_REMOVED      * filter on /a</li>      *<li>additionally for paths with globs (eg /a/b/**{@code /}*.jsp)      * it adds a deep NODE_REMOVED filter explicitly for that path      * using the same method as withIncludeSubtreeOnRemove does, but only      * limited to said path. So in this case you get a NODE_REMOVED      * for all *.jsp that are deleted in a subtree individually</li>      *</ul>      *<p>      * Note that unlike 'normal' include and exclude paths, this variant      * doesn't apply Oak's NamePathMapper on the ancestors of the      * registers paths.      *<p>      * Also note that this might disable 'observation prefiltering based on paths'       * (OAK-4796) on this listener.      * @return this filter with the filter change applied      */
specifier|public
specifier|abstract
name|OakEventFilter
name|withIncludeAncestorsRemove
parameter_list|()
function_decl|;
comment|/**      * This flag causes remove events to be sent for all nodes and properties      * of an entire subtree (hence use with care!).      *<p>      * It is only applied when a parent node is actually removed. For       * a parent node move this is not applied.      * @return this filter with the filter change applied      */
specifier|public
specifier|abstract
name|OakEventFilter
name|withIncludeSubtreeOnRemove
parameter_list|()
function_decl|;
comment|/**      * Adds the provided glob paths to the set of include paths.      *<p>      * The definition of a glob path is      *<a href="https://jackrabbit.apache.org/oak/docs/apidocs/org/apache/jackrabbit/oak/plugins/observation/filter/GlobbingPathFilter.html">here</a>      *<p>      * Note that unlike 'normal' include and exclude paths, this variant      * doesn't apply Oak's NamePathMapper.      *<p>      * This filter property is added in 'or' mode.      *       * @param globPath      *            glob path that should be added as include path pattern. Note      *            that the NamePathMapper is not applied on this globPath.      * @return this filter with the filter change applied      */
specifier|public
specifier|abstract
name|OakEventFilter
name|withIncludeGlobPaths
parameter_list|(
name|String
modifier|...
name|globPaths
parameter_list|)
function_decl|;
comment|/**      * Greedy aggregating filter which upon first (hence greedy) hit of provided      * nodeTypes checks if the child subtree leading to the actual change      * matches any of the provided relativeGlobPaths.      *<p>      * Note that unlike 'normal' include and exclude paths, this variant      * doesn't apply Oak's NamePathMapper.      *<p>      * This filter property is added in 'and' mode.      *       * @param nodeTypes      *            note that these nodeTypes are not mapped to oak nor validated      * @param relativeGlobPaths      *            glob paths that are added to the set of include paths.      *            To match the node with the desired node type itself, add       *            an empty String ("") as one of the relativeGlobPaths too.      *            Note that Oak's NamePathMapper is not applied to these relativeGlobPaths.      * @return this filter with the filter change applied      */
specifier|public
specifier|abstract
name|OakEventFilter
name|withNodeTypeAggregate
parameter_list|(
specifier|final
name|String
index|[]
name|nodeTypes
parameter_list|,
specifier|final
name|String
index|[]
name|relativeGlobPaths
parameter_list|)
function_decl|;
block|}
end_class

end_unit

