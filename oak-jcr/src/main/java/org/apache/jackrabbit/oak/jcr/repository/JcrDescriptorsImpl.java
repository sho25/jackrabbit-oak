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
operator|.
name|repository
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
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
name|Descriptors
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
name|util
operator|.
name|GenericDescriptors
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_LOCKING_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_XML_EXPORT_SUPPORTED
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|Repository
operator|.
name|OPTION_XML_IMPORT_SUPPORTED
import|;
end_import

begin_comment
comment|/**  * The {@code JcrDescriptorsImpl} extend the {@link org.apache.jackrabbit.oak.util.GenericDescriptors} by automatically marking some of the JCR  * features as supported.  */
end_comment

begin_class
specifier|public
class|class
name|JcrDescriptorsImpl
extends|extends
name|GenericDescriptors
block|{
specifier|public
name|JcrDescriptorsImpl
parameter_list|(
name|Descriptors
name|base
parameter_list|,
name|ValueFactory
name|valueFactory
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
comment|// add the descriptors of the features that are provided by the JCR layer
specifier|final
name|Value
name|trueValue
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|put
argument_list|(
name|OPTION_LOCKING_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OPTION_XML_EXPORT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|OPTION_XML_IMPORT_SUPPORTED
argument_list|,
name|trueValue
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

