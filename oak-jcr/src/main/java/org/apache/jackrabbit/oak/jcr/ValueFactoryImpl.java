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
name|jcr
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
name|value
operator|.
name|AbstractValueFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFormatException
import|;
end_import

begin_comment
comment|/**  * ValueFactoryImpl...  */
end_comment

begin_class
class|class
name|ValueFactoryImpl
extends|extends
name|AbstractValueFactory
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ValueFactoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|ValueFactoryImpl
parameter_list|()
block|{      }
annotation|@
name|Override
specifier|protected
name|void
name|checkPathFormat
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ValueFormatException
block|{
comment|// TODO : path validation
block|}
annotation|@
name|Override
specifier|protected
name|void
name|checkNameFormat
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ValueFormatException
block|{
comment|// TODO : name validation
block|}
block|}
end_class

end_unit

