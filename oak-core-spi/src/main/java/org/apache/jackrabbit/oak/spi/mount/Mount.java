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
name|spi
operator|.
name|mount
package|;
end_package

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * A ContentRepository represents one big tree. A Mount  * refers to a set of paths in that tree which are possibly  * stored in a separate physical persistent stores. In a  * default setup all paths belong to a default Mount.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|Mount
block|{
comment|/**      * Name of the mount. If this<code>Mount</code>      * is the default mount, an empty string is returned      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Checks whether the mount is marked as read only.      *      * @return true if the mount is read only.      */
name|boolean
name|isReadOnly
parameter_list|()
function_decl|;
comment|/**      * Checks whether current mount is the default mount.      * Default mount includes the root path and all other      * paths which are not part of any other mount      *      * @return true if this mount represents the      * default mount      */
name|boolean
name|isDefault
parameter_list|()
function_decl|;
comment|/**      * Returns fragment name which can be used to construct node name      * used for storing meta content belonging to path under this      *<code>Mount</code>. Such a node name would be used by NodeStore      * to determine the storage for nodes under those paths.      *      *<p>Fragment name  is formatted as 'oak:mount-&lt;mount name&gt;'      *      *<p>For e.g. for mount name 'private' the fragment name would be      *<code>oak:mount-private</code>. This can be then used to construct      * node name like<code>oak:mount-private-index</code> and then any derived      * content for path under this mount would be stored as child node under      *<i>oak:mount-private-index</i> like<code>/fooIndex/oak:mount-private-index/foo</code>.      * Such paths would then be stored in a separate store which would only be      * storing paths belonging to that mount      *      *<p>If this<code>Mount</code> is the default mount, an empty string is returned      *      * @return node name prefix which can be used      */
name|String
name|getPathFragmentName
parameter_list|()
function_decl|;
comment|/**      * Checks if this mount supports mounting nodes containing the fragment      * (see {@link #getPathFragmentName()}) under the given path.      *      * @param path ancestor path      * @return true if the path fragment mounts are supported in the given subtree      */
name|boolean
name|isSupportFragment
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Checks if given path belongs to this<code>Mount</code>      *      * @param path path to check      * @return true if path belong to this mount      */
name|boolean
name|isMounted
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Checks if this mount falls under given path. For e.g. if a      * mount consist of '/etc/config'. Then if path is      *<ul>      *<li>/etc - Then it returns true</li>      *<li>/etc/config - Then it returns false</li>      *<li>/lib - Then it returns false</li>      *</ul>      *      * @param path path to check      * @return true if this Mount is rooted under given path      */
name|boolean
name|isUnder
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
name|boolean
name|isDirectlyUnder
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

