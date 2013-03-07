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
name|spi
operator|.
name|xml
package|;
end_package

begin_comment
comment|/**  * Information about a node being imported. This class is used  * by the XML import handlers to pass the parsed node information to the  * import process.  *<p>  * An instance of this class is simply a container for the node name,  * node uuidentifier, and the node type information. See the {@link PropInfo}  * class for the related carrier of property information.  */
end_comment

begin_class
specifier|public
class|class
name|NodeInfo
block|{
comment|/**      * Name of the node being imported.      */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Name of the primary type of the node being imported.      */
specifier|private
specifier|final
name|String
name|primaryTypeName
decl_stmt|;
comment|/**      * Names of the mixin types of the node being imported.      */
specifier|private
specifier|final
name|String
index|[]
name|mixinTypeNames
decl_stmt|;
comment|/**      * UUID of the node being imported.      */
specifier|private
specifier|final
name|String
name|uuid
decl_stmt|;
comment|/**      * Creates a node information instance.      *      * @param name name of the node being imported      * @param primaryTypeName name of the primary type of the node being imported      * @param mixinTypeNames names of the mixin types of the node being imported      * @param uuid uuid of the node being imported      */
specifier|public
name|NodeInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|primaryTypeName
parameter_list|,
name|String
index|[]
name|mixinTypeNames
parameter_list|,
name|String
name|uuid
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|primaryTypeName
operator|=
name|primaryTypeName
expr_stmt|;
name|this
operator|.
name|mixinTypeNames
operator|=
name|mixinTypeNames
expr_stmt|;
name|this
operator|.
name|uuid
operator|=
name|uuid
expr_stmt|;
block|}
comment|/**      * Returns the name of the node being imported.      *      * @return node name      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Returns the name of the primary type of the node being imported.      *      * @return primary type name      */
specifier|public
name|String
name|getPrimaryTypeName
parameter_list|()
block|{
return|return
name|primaryTypeName
return|;
block|}
comment|/**      * Returns the names of the mixin types of the node being imported.      *      * @return mixin type names      */
specifier|public
name|String
index|[]
name|getMixinTypeNames
parameter_list|()
block|{
return|return
name|mixinTypeNames
return|;
block|}
comment|/**      * Returns the uuid of the node being imported.      *      * @return node uuid      */
specifier|public
name|String
name|getUUID
parameter_list|()
block|{
return|return
name|uuid
return|;
block|}
block|}
end_class

end_unit

