package ch.frimtec.raspicontrol.application.demo;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class RaspicontrolDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(RaspicontrolDemoApplication.class);
    private final GpioPinDigitalOutput[] relais;
    private final I2CBus i2c;
    private final I2CDevice brightPi;
    private Ads1115 adc;
    private byte brightPiState = 0x01;

    public static void main(String[] args) {
        SpringApplication.run(RaspicontrolDemoApplication.class, args);
    }

    public RaspicontrolDemoApplication() {
        shopPi4JSystemInfo();
        relais = accessHw();
        try {
            for (PinState state : PinState.allStates()) {
                for (GpioPinDigitalOutput relay : relais) {
                    logger.info("Set {} to {}", relay.getName(), state.getName());
                    relay.setState(state);
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            logger.warn("Main thread was interrupted.");
        }
        i2c = createI2c();
        brightPi = createI2cDevice(i2c, 0x70);

        try {
            adc = new Ads1115(i2c.getDevice(Ads1115.ADS1115_ADDRESS_0x48));
        } catch (IOException e) {
            logger.error("Error ADC", e);
        }
    }

    private static void shopPi4JSystemInfo() {
        try {
            // display a few of the available system information properties
            System.out.println("----------------------------------------------------");
            System.out.println("PLATFORM INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("Platform Name     :  " + PlatformManager.getPlatform().getLabel());
            System.out.println("Platform ID       :  " + PlatformManager.getPlatform().getId());
            System.out.println("----------------------------------------------------");
            System.out.println("HARDWARE INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("Serial Number     :  " + SystemInfo.getSerial());
            System.out.println("CPU Revision      :  " + SystemInfo.getCpuRevision());
            System.out.println("CPU Architecture  :  " + SystemInfo.getCpuArchitecture());
            System.out.println("CPU Part          :  " + SystemInfo.getCpuPart());
            System.out.println("CPU Temperature   :  " + SystemInfo.getCpuTemperature());
            System.out.println("CPU Core Voltage  :  " + SystemInfo.getCpuVoltage());
            System.out.println("CPU Model Name    :  " + SystemInfo.getModelName());
            System.out.println("Processor         :  " + SystemInfo.getProcessor());
            System.out.println("Hardware          :  " + SystemInfo.getHardware());
            System.out.println("Hardware Revision :  " + SystemInfo.getRevision());
            System.out.println("Is Hard Float ABI :  " + SystemInfo.isHardFloatAbi());
            System.out.println("Board Type        :  " + SystemInfo.getBoardType().name());

            System.out.println("----------------------------------------------------");
            System.out.println("MEMORY INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("Total Memory      :  " + SystemInfo.getMemoryTotal());
            System.out.println("Used Memory       :  " + SystemInfo.getMemoryUsed());
            System.out.println("Free Memory       :  " + SystemInfo.getMemoryFree());
            System.out.println("Shared Memory     :  " + SystemInfo.getMemoryShared());
            System.out.println("Memory Buffers    :  " + SystemInfo.getMemoryBuffers());
            System.out.println("Cached Memory     :  " + SystemInfo.getMemoryCached());
            System.out.println("SDRAM_C Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_C());
            System.out.println("SDRAM_I Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_I());
            System.out.println("SDRAM_P Voltage   :  " + SystemInfo.getMemoryVoltageSDRam_P());

            System.out.println("----------------------------------------------------");
            System.out.println("OPERATING SYSTEM INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("OS Name           :  " + SystemInfo.getOsName());
            System.out.println("OS Version        :  " + SystemInfo.getOsVersion());
            System.out.println("OS Architecture   :  " + SystemInfo.getOsArch());
            System.out.println("OS Firmware Build :  " + SystemInfo.getOsFirmwareBuild());
            System.out.println("OS Firmware Date  :  " + SystemInfo.getOsFirmwareDate());

            System.out.println("----------------------------------------------------");
            System.out.println("JAVA ENVIRONMENT INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("Java Vendor       :  " + SystemInfo.getJavaVendor());
            System.out.println("Java Vendor URL   :  " + SystemInfo.getJavaVendorUrl());
            System.out.println("Java Version      :  " + SystemInfo.getJavaVersion());
            System.out.println("Java VM           :  " + SystemInfo.getJavaVirtualMachine());
            System.out.println("Java Runtime      :  " + SystemInfo.getJavaRuntime());

            System.out.println("----------------------------------------------------");
            System.out.println("NETWORK INFO");
            System.out.println("----------------------------------------------------");

            // display some of the network information
            System.out.println("Hostname          :  " + NetworkInfo.getHostname());
            for (String ipAddress : NetworkInfo.getIPAddresses())
                System.out.println("IP Addresses      :  " + ipAddress);
            for (String fqdn : NetworkInfo.getFQDNs())
                System.out.println("FQDN              :  " + fqdn);
            for (String nameserver : NetworkInfo.getNameservers())
                System.out.println("Nameserver        :  " + nameserver);

            System.out.println("----------------------------------------------------");
            System.out.println("CODEC INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("H264 Codec Enabled:  " + SystemInfo.getCodecH264Enabled());
            System.out.println("MPG2 Codec Enabled:  " + SystemInfo.getCodecMPG2Enabled());
            System.out.println("WVC1 Codec Enabled:  " + SystemInfo.getCodecWVC1Enabled());

            System.out.println("----------------------------------------------------");
            System.out.println("CLOCK INFO");
            System.out.println("----------------------------------------------------");
            System.out.println("ARM Frequency     :  " + SystemInfo.getClockFrequencyArm());
            System.out.println("CORE Frequency    :  " + SystemInfo.getClockFrequencyCore());
            System.out.println("H264 Frequency    :  " + SystemInfo.getClockFrequencyH264());
            System.out.println("ISP Frequency     :  " + SystemInfo.getClockFrequencyISP());
            System.out.println("V3D Frequency     :  " + SystemInfo.getClockFrequencyV3D());
            System.out.println("UART Frequency    :  " + SystemInfo.getClockFrequencyUART());
            System.out.println("PWM Frequency     :  " + SystemInfo.getClockFrequencyPWM());
            System.out.println("EMMC Frequency    :  " + SystemInfo.getClockFrequencyEMMC());
            System.out.println("Pixel Frequency   :  " + SystemInfo.getClockFrequencyPixel());
            System.out.println("VEC Frequency     :  " + SystemInfo.getClockFrequencyVEC());
            System.out.println("HDMI Frequency    :  " + SystemInfo.getClockFrequencyHDMI());
            System.out.println("DPI Frequency     :  " + SystemInfo.getClockFrequencyDPI());
            System.out.println();
            System.out.println();
            System.out.println("Exiting SystemInfoExample");
        } catch (Exception e) {
            logger.error("Error reading Pi4J System-Info", e);
        }

    }

    private static GpioPinDigitalOutput[] accessHw() {
        try {
            GpioController gpio = GpioFactory.getInstance();
            GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
            GpioPinDigitalOutput relais1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.HIGH);
            GpioPinDigitalOutput relais2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, PinState.HIGH);
            GpioPinDigitalOutput relais3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, PinState.HIGH);
            System.out.println(String.format("PIN %s: %s", myButton.getName(), myButton.getState()));
            return new GpioPinDigitalOutput[]{relais1, relais2, relais3};
        } catch (Throwable e) {
            logger.error("Error accessing GPIO", e);
            return new GpioPinDigitalOutput[0];
        }
    }

    private static I2CBus createI2c() {
        try {
            return I2CFactory.getInstance(I2CBus.BUS_1);
        } catch (Throwable e) {
            logger.error("Error accessing I2C", e);
            return null;
        }
    }

    private I2CDevice createI2cDevice(I2CBus i2c, int address) {
        try {
            return i2c.getDevice(address);
        } catch (Throwable e) {
            logger.error("Error creating I2C device", e);
            return null;
        }
    }

    @Scheduled(fixedRate = 5000)
    public void keepAlive() {
        logger.info("Keep alive.");
        try {
//            brightPi.write(0, brightPiState);
//            brightPiState = (byte) (brightPiState * 0x02);
//            if (brightPiState == 0x00) {
//                brightPiState = 0x01;
//            }

            int value0 = adc.getImmediateValue(Ads1115.Pin.PIN0);
            int value3 = adc.getImmediateValue(Ads1115.Pin.PIN3);
            System.out.println("ADC raw values 0: " + value0 + " ; 3: " + value3);

        } catch (IOException e) {
            logger.error("Error writting to I2C device", e);
        }
    }
}
