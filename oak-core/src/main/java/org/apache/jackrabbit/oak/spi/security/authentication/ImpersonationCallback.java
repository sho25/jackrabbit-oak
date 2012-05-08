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
name|security
operator|.
name|authentication
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
import|;
end_import

begin_comment
comment|/**  * Callback for a {@link javax.security.auth.callback.CallbackHandler} to ask  * for a the impersonating {@link javax.security.auth.Subject} to create a  * {@link javax.jcr.Session} to access the {@link javax.jcr.Repository}.  */
end_comment

begin_class
specifier|public
class|class
name|ImpersonationCallback
implements|implements
name|Callback
block|{
comment|/**      * The impersonating {@link javax.security.auth.Subject}.      */
specifier|private
name|Subject
name|impersonatingSubject
decl_stmt|;
comment|/**      * Sets the impersonator in this callback.      *      * @param impersonatingSubject The impersonator to set on this callback.      */
specifier|public
name|void
name|setImpersonator
parameter_list|(
name|Subject
name|impersonatingSubject
parameter_list|)
block|{
name|this
operator|.
name|impersonatingSubject
operator|=
name|impersonatingSubject
expr_stmt|;
block|}
comment|/**      * Returns the impersonator {@link Subject} set on this callback or      *<code>null</code> if not set.      *      * @return the impersonator {@link Subject} set on this callback or      *<code>null</code> if not set.      */
specifier|public
name|Subject
name|getImpersonator
parameter_list|()
block|{
return|return
name|impersonatingSubject
return|;
block|}
block|}
end_class

end_unit

