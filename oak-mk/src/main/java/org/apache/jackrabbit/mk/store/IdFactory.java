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
name|mk
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_comment
comment|/**  * Create new internal content object ids based on serialized data.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|IdFactory
block|{
comment|/**      * Creates a new id based on the specified serialized data.      *<p>      * The general contract of {@code createContentId} is:      *<p>      * {@code createId(data1).equals(createId(data2)) == Arrays.equals(data1, data2)}      *      * @param serialized serialized data      * @return raw node id as byte array      * @throws Exception if an error occurs      */
specifier|public
name|byte
index|[]
name|createContentId
parameter_list|(
name|byte
index|[]
name|serialized
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|digest
argument_list|(
name|serialized
argument_list|)
return|;
block|}
comment|/**      * Return a digest for some data.      *       * @param data data      * @return digest      */
specifier|protected
name|byte
index|[]
name|digest
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-1"
argument_list|)
operator|.
name|digest
argument_list|(
name|data
argument_list|)
return|;
block|}
comment|/**      * Return the default factory that will create node and revision ids based      * on their content.       *       * @return factory      */
specifier|public
specifier|static
name|IdFactory
name|getDigestFactory
parameter_list|()
block|{
return|return
operator|new
name|IdFactory
argument_list|()
block|{}
return|;
block|}
block|}
end_class

end_unit

