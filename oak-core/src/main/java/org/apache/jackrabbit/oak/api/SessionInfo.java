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
name|api
package|;
end_package

begin_comment
comment|/**  * The {@code SessionInfo} TODO... describe how obtained, when disposed, used for communication with oak-api, identification, authorization....  */
end_comment

begin_interface
specifier|public
interface|interface
name|SessionInfo
block|{
comment|/**      * Return the user ID to be exposed on the JCR Session object. It refers      * to the ID of the user associated with the Credentials passed to the      * repository login.      *      * @return the user ID such as exposed on the JCR Session object.      */
name|String
name|getUserID
parameter_list|()
function_decl|;
comment|/**      * Returns the attribute names associated with this instance.      *      * @return The attribute names with that instance or an empty array if      * no attributes are present.      */
name|String
index|[]
name|getAttributeNames
parameter_list|()
function_decl|;
comment|/**      * Returns the attribute with the given name or {@code null} if no attribute      * with that {@code attributeName} exists.      *      * @param attributeName The attribute name.      * @return The attribute or {@code null}.      */
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
function_decl|;
comment|/**      * Returns the current revision the associated session is operating on.      * Unless otherwise specified the revision is set to the current head      * revision upon {@code SessionInfo} creation. Later on in the lifecycle      * of this {@code SessionInfo} the revision will be reset to match the      * latest state after successful commit of modifications or if the associated      * session is being refreshed.      *      * @return the revision The current revision.      */
name|String
name|getRevision
parameter_list|()
function_decl|;
comment|/**      * The immutable name of the workspace this {@code SessionInfo} instance has      * been created for. If no workspace name has been specified during      * repository login this method will return the name of the default      * workspace.      *      * @return name of the workspace this instance has been created for.      */
name|String
name|getWorkspaceName
parameter_list|()
function_decl|;
comment|/**      * Dispose this instance of {@code SessionInfo} as the associated      * JCR Session instance was logged out. This method allows an implementation      * to free any resources that may possibly be associated with this instance.      */
name|void
name|dispose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

