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
name|mongomk
operator|.
name|impl
operator|.
name|json
package|;
end_package

begin_comment
comment|/**  * The event callback of the parser.  *  *<p>  * Each event callback has an empty default implementation. An implementor may choose the appropriate methods to  * overwrite.  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|DefaultJsopHandler
block|{
comment|/**      * Event: A node has been added.      *      * @param parentPath The path where the node was added to.      * @param name The name of the added node.      */
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// No-op
block|}
comment|/**      * Event: A node was copied.      *      * @param rootPath The root path where the copy took place.      * @param oldPath The old path of the node (relative to the root path).      * @param newPath The new path of the node (relative to the root path).      */
specifier|public
name|void
name|nodeCopied
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|String
name|oldPath
parameter_list|,
name|String
name|newPath
parameter_list|)
block|{
comment|// No-op
block|}
comment|/**      * Event: A node was moved.      *      * @param rootPath The root path where the copy took place.      * @param oldPath The old path of the node (relative to the root path).      * @param newPath The new path of the node (relative to the root path).      */
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|String
name|oldPath
parameter_list|,
name|String
name|newPath
parameter_list|)
block|{
comment|// No-op
block|}
comment|/**      * Event: A node was removed.      *      * @param parentPath The path where the node was removed from.      * @param name The name of the node.      */
specifier|public
name|void
name|nodeRemoved
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// No-op
block|}
comment|/**      * Event: A property was set.      *      * @param path The path of the node where the property was set.      * @param key The key of the property.      * @param value The value of the property.      */
specifier|public
name|void
name|propertySet
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// No-op
block|}
block|}
end_class

end_unit

