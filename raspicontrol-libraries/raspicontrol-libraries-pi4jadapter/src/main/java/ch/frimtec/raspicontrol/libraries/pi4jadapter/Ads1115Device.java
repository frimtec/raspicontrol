package ch.frimtec.raspicontrol.libraries.pi4jadapter;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ch.frimtec.raspicontrol.libraries.pi4jadapter.Ads1115Device.ProgrammableGainAmplifierValue.PGA_6_144V;

public class Ads1115Device {
    public enum Pin {
        PIN0(0),
        PIN1(1),
        PIN2(2),
        PIN3(3);

        final int address;

        Pin(int address) {
            this.address = address;
        }
    }

    public class AdcPin {
        private final Pin pin;

        private AdcPin(Pin pin, ProgrammableGainAmplifierValue gainAmplifierValue) {
            this.pin = pin;
            if (Ads1115Device.this.pga.containsKey(pin)) {
                throw new IllegalStateException("Pin already in use: " + pin);
            }
            Ads1115Device.this.setGainAmplifierValue(pin, gainAmplifierValue);
        }

        public int getImmediateValue() throws IOException {
            return Ads1115Device.this.getImmediateValue(pin);
        }
    }

    public AdcPin openAdcPin(Pin pin, ProgrammableGainAmplifierValue gainAmplifierValue) {
        return new AdcPin(pin, gainAmplifierValue);
    }

    // =======================================================================
    // ADS1115 I2C ADDRESS
    // =======================================================================
    public static final int ADS1115_ADDRESS_0x48 = 0x48; // ADDRESS 1 : 0x48 (1001000) ADR -> GND
    public static final int ADS1115_ADDRESS_0x49 = 0x49; // ADDRESS 2 : 0x49 (1001001) ADR -> VDD
    public static final int ADS1115_ADDRESS_0x4A = 0x4A; // ADDRESS 3 : 0x4A (1001010) ADR -> SDA
    public static final int ADS1115_ADDRESS_0x4B = 0x4B; // ADDRESS 4 : 0x4B (1001011) ADR -> SCL

    // =======================================================================
    // ADS1115 VALUE RANGES
    // =======================================================================
    public static final int ADS1115_RANGE_MAX_VALUE = 32767; //0x7FFF (16 bits)
    public static final int ADS1115_RANGE_MIN_VALUE = -32768; //0xFFFF (16 bits)

    // =======================================================================
    // CONVERSION DELAY (in mS)
    // =======================================================================
    protected static final int ADS1115_CONVERSIONDELAY = 0x08;

    // =======================================================================
    // POINTER REGISTER
    // =======================================================================
    protected static final int ADS1x15_REG_POINTER_MASK = 0x03;
    protected static final int ADS1x15_REG_POINTER_CONVERT = 0x00;
    protected static final int ADS1x15_REG_POINTER_CONFIG = 0x01;
    protected static final int ADS1x15_REG_POINTER_LOWTHRESH = 0x02;
    protected static final int ADS1x15_REG_POINTER_HITHRESH = 0x03;

    // =======================================================================
    // CONFIG REGISTER
    // =======================================================================
    protected static final int ADS1x15_REG_CONFIG_OS_MASK = 0x8000;
    protected static final int ADS1x15_REG_CONFIG_OS_SINGLE = 0x8000;  // Write: Set to start a single-conversion
    protected static final int ADS1x15_REG_CONFIG_OS_BUSY = 0x0000;  // Read: Bit = 0 when conversion is in progress
    protected static final int ADS1x15_REG_CONFIG_OS_NOTBUSY = 0x8000;  // Read: Bit = 1 when device is not performing a conversion

    protected static final int ADS1x15_REG_CONFIG_MUX_MASK = 0x7000;
    protected static final int ADS1x15_REG_CONFIG_MUX_DIFF_0_1 = 0x0000;  // Differential P = AIN0, N = AIN1 (default)
    protected static final int ADS1x15_REG_CONFIG_MUX_DIFF_0_3 = 0x1000;  // Differential P = AIN0, N = AIN3
    protected static final int ADS1x15_REG_CONFIG_MUX_DIFF_1_3 = 0x2000;  // Differential P = AIN1, N = AIN3
    protected static final int ADS1x15_REG_CONFIG_MUX_DIFF_2_3 = 0x3000;  // Differential P = AIN2, N = AIN3
    protected static final int ADS1x15_REG_CONFIG_MUX_SINGLE_0 = 0x4000;  // Single-ended AIN0
    protected static final int ADS1x15_REG_CONFIG_MUX_SINGLE_1 = 0x5000;  // Single-ended AIN1
    protected static final int ADS1x15_REG_CONFIG_MUX_SINGLE_2 = 0x6000;  // Single-ended AIN2
    protected static final int ADS1x15_REG_CONFIG_MUX_SINGLE_3 = 0x7000;  // Single-ended AIN3

    protected static final int ADS1x15_REG_CONFIG_PGA_MASK = 0x0E00;
    protected static final int ADS1x15_REG_CONFIG_PGA_6_144V = 0x0000;  // +/-6.144V range
    protected static final int ADS1x15_REG_CONFIG_PGA_4_096V = 0x0200;  // +/-4.096V range
    protected static final int ADS1x15_REG_CONFIG_PGA_2_048V = 0x0400;  // +/-2.048V range (default)
    protected static final int ADS1x15_REG_CONFIG_PGA_1_024V = 0x0600;  // +/-1.024V range
    protected static final int ADS1x15_REG_CONFIG_PGA_0_512V = 0x0800;  // +/-0.512V range
    protected static final int ADS1x15_REG_CONFIG_PGA_0_256V = 0x0A00;  // +/-0.256V range

    protected static final int ADS1x15_REG_CONFIG_MODE_MASK = 0x0100;
    protected static final int ADS1x15_REG_CONFIG_MODE_CONTIN = 0x0000;  // Continuous conversion mode
    protected static final int ADS1x15_REG_CONFIG_MODE_SINGLE = 0x0100;  // Power-down single-shot mode (default)

    protected static final int ADS1x15_REG_CONFIG_DR_MASK = 0x00E0;
    protected static final int ADS1x15_REG_CONFIG_DR_128SPS = 0x0000;  // 128 samples per second
    protected static final int ADS1x15_REG_CONFIG_DR_250SPS = 0x0020;  // 250 samples per second
    protected static final int ADS1x15_REG_CONFIG_DR_490SPS = 0x0040;  // 490 samples per second
    protected static final int ADS1x15_REG_CONFIG_DR_920SPS = 0x0060;  // 920 samples per second
    protected static final int ADS1x15_REG_CONFIG_DR_1600SPS = 0x0080;  // 1600 samples per second (default)
    protected static final int ADS1x15_REG_CONFIG_DR_2400SPS = 0x00A0;  // 2400 samples per second
    protected static final int ADS1x15_REG_CONFIG_DR_3300SPS = 0x00C0;  // 3300 samples per second

    protected static final int ADS1x15_REG_CONFIG_CMODE_MASK = 0x0010;
    protected static final int ADS1x15_REG_CONFIG_CMODE_TRAD = 0x0000;  // Traditional comparator with hysteresis (default)
    protected static final int ADS1x15_REG_CONFIG_CMODE_WINDOW = 0x0010;  // Window comparator

    protected static final int ADS1x15_REG_CONFIG_CPOL_MASK = 0x0008;
    protected static final int ADS1x15_REG_CONFIG_CPOL_ACTVLOW = 0x0000;  // ALERT/RDY pin is low when active (default)
    protected static final int ADS1x15_REG_CONFIG_CPOL_ACTVHI = 0x0008;  // ALERT/RDY pin is high when active

    protected static final int ADS1x15_REG_CONFIG_CLAT_MASK = 0x0004;  // Determines if ALERT/RDY pin latches once asserted
    protected static final int ADS1x15_REG_CONFIG_CLAT_NONLAT = 0x0000;  // Non-latching comparator (default)
    protected static final int ADS1x15_REG_CONFIG_CLAT_LATCH = 0x0004;  // Latching comparator

    protected static final int ADS1x15_REG_CONFIG_CQUE_MASK = 0x0003;
    protected static final int ADS1x15_REG_CONFIG_CQUE_1CONV = 0x0000;  // Assert ALERT/RDY after one conversions
    protected static final int ADS1x15_REG_CONFIG_CQUE_2CONV = 0x0001;  // Assert ALERT/RDY after two conversions
    protected static final int ADS1x15_REG_CONFIG_CQUE_4CONV = 0x0002;  // Assert ALERT/RDY after four conversions
    protected static final int ADS1x15_REG_CONFIG_CQUE_NONE = 0x0003;  // Disable the comparator and put ALERT/RDY in high state (default)


    public enum ProgrammableGainAmplifierValue {
        PGA_6_144V(6.144, ADS1x15_REG_CONFIG_PGA_6_144V),  // +/-6.144V range
        PGA_4_096V(4.096, ADS1x15_REG_CONFIG_PGA_4_096V),  // +/-4.096V range
        PGA_2_048V(2.048, ADS1x15_REG_CONFIG_PGA_2_048V),  // +/-2.048V range (default)
        PGA_1_024V(1.024, ADS1x15_REG_CONFIG_PGA_1_024V),  // +/-1.024V range
        PGA_0_512V(0.512, ADS1x15_REG_CONFIG_PGA_0_512V),  // +/-0.512V range
        PGA_0_256V(0.256, ADS1x15_REG_CONFIG_PGA_0_256V);   // +/-0.256V range

        private final double voltage;
        private final int configValue;

        ProgrammableGainAmplifierValue(double voltage, int configValue) {
            this.voltage = voltage;
            this.configValue = configValue;
        }

        public double getVoltage() {
            return this.voltage;
        }

        public int getConfigValue() {
            return this.configValue;
        }
    }

    private I2CDevice device;

    // defines the PGA used when reading the analog input value
    private final Map<Pin, ProgrammableGainAmplifierValue> pga = new HashMap<>();

    public Ads1115Device(I2CDevice device) {
        this.device = device;
    }

    private void setGainAmplifierValue(Pin pin, ProgrammableGainAmplifierValue programmableGainAmplifierValue) {
        this.pga.put(pin, programmableGainAmplifierValue);
    }

    private int getImmediateValue(Pin pin) throws IOException {

        // Start with default values
        int config = ADS1x15_REG_CONFIG_CQUE_NONE | // Disable the comparator (default val)
                ADS1x15_REG_CONFIG_CLAT_NONLAT | // Non-latching (default val)
                ADS1x15_REG_CONFIG_CPOL_ACTVLOW | // Alert/Rdy active low   (default val)
                ADS1x15_REG_CONFIG_CMODE_TRAD | // Traditional comparator (default val)
                ADS1x15_REG_CONFIG_DR_1600SPS | // 1600 samples per second (default)
                ADS1x15_REG_CONFIG_MODE_SINGLE;   // Single-shot mode (default)

        // Set PGA/voltage range
        config |= pga.get(pin).getConfigValue();  // +/- 6.144V range (limited to VDD +0.3V max!)

        // Set single-ended input channel
        switch (pin.address) {
            case (0):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_0;
                break;
            case (1):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_1;
                break;
            case (2):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_2;
                break;
            case (3):
                config |= ADS1x15_REG_CONFIG_MUX_SINGLE_3;
                break;
        }

        // Set 'start single-conversion' bit
        config |= ADS1x15_REG_CONFIG_OS_SINGLE;

        // Write config register to the ADC
        writeRegister(ADS1x15_REG_POINTER_CONFIG, config);

        // Wait for the conversion to complete
        try {
            if (ADS1115_CONVERSIONDELAY > 0) {
                Thread.sleep(ADS1115_CONVERSIONDELAY);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // read the conversion results
        return readRegister(ADS1x15_REG_POINTER_CONVERT);
    }

    ;

    private void writeRegister(int register, int value) throws IOException {

        // create packet in data buffer
        byte packet[] = new byte[3];
        packet[0] = (byte) (register);     // register byte
        packet[1] = (byte) (value >> 8);     // value MSB
        packet[2] = (byte) (value & 0xFF); // value LSB

        // write data to I2C device
        device.write(packet, 0, 3);
    }

    // Writes 16-bits to the specified destination register
    private int readRegister(int register) throws IOException {

        device.write((byte) register);

        // create data buffer for receive data
        byte buffer[] = new byte[2];  // receive 16 bits (2 bytes)
        int byteCount = 0;
        try {
            byteCount = device.read(buffer, 0, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (byteCount == 2) {
            short value = getShort(buffer, 0);
            return value;
        } else {
            return 0;
        }
    }

    private static short getShort(byte[] arr, int off) {
        return (short) (arr[off] << 8 & 0xFF00 | arr[off + 1] & 0xFF);
    }

}
