JBOSS code generation plugin

------------------------------------------------------------------------------------------------------------------------
Goals
------------------------------------------------------------------------------------------------------------------------
wsprovide - generates WSDL files for use with axis tools based on JBoss WS code and annotations

------------------------------------------------------------------------------------------------------------------------
POM usage:

                     <configuration>
                                        <endpoints>
                                                <item>com.xxx.integration.service.CustomerSearch</item>
                                                <item>com.xxx.integration.service.CustomerUpdate</item>
                                                <item>com.xxx.integration.service.OrderHistory</item>
                                        </endpoints>
                                </configuration>
                                <executions>
                                        <execution>
                                                <goals>
                                                        <goal>wsprovide</goal>
                                                </goals>
                                        </execution>
                                </executions>
------------------------------------------------------------------------------------------------------------------------

