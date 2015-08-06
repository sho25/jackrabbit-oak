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
name|j2ee
package|;
end_package

begin_comment
comment|/**  * Exception for signaling that the JCR API is not available.  */
end_comment

begin_class
specifier|public
class|class
name|JcrApiNotFoundException
extends|extends
name|ServletExceptionWithCause
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6439777923943394980L
decl_stmt|;
comment|/**      * Creates an exception to signal that the JCR API is not available.      *      * @param e the specific exception that indicates the lack of the JCR API      */
specifier|public
name|JcrApiNotFoundException
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|super
argument_list|(
literal|"JCR API (jcr-1.0.jar) not available in the classpath"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

