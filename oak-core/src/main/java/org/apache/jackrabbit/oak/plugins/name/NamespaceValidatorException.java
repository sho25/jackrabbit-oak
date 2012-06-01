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
name|name
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
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
name|api
operator|.
name|CommitFailedException
import|;
end_import

begin_class
class|class
name|NamespaceValidatorException
extends|extends
name|CommitFailedException
block|{
specifier|public
name|NamespaceValidatorException
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|prefix
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NamespaceException
name|getNamespaceException
parameter_list|()
block|{
return|return
operator|new
name|NamespaceException
argument_list|(
name|getMessage
argument_list|()
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

